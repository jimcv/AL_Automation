package main;

import static main.PositionData.*;
import map_grind.*;

public class Handler extends Thread {
	
	private Grinder_OFNB OFNBGrinder;
	private Grinder_TFB	TFBGrinder;
	private int curGrinder;						// 1 is OFNB, 2 is TFB, 0 is not grinding
	
	/**
	 * Constructor
	 */
	public Handler() {
		this.OFNBGrinder = null;
		this.TFBGrinder = null;
		this.curGrinder = 0;
	}
	
	/**
	 * Continuously scan for task
	 */
	public void run() {
		while (true) {
			pause(200);
			String cmd = Start.GUI.task;
			// NORTH
			if (cmd.equals("Set Data Directory")) {
				this.cmdSetDataDir();
				Start.GUI.task = "";
			}
			// SOUTH
			else if (cmd.equals("Up")) {
				Start.GUI.currentTask.setText("Moving up");
				this.cmdUp();
				Start.GUI.task = "";
				Start.GUI.currentTask.setText("");
			} else if (cmd.equals("Down")) {
				Start.GUI.currentTask.setText("Moving down");
				this.cmdDown();
				Start.GUI.task = "";
				Start.GUI.currentTask.setText("");
			} else if (cmd.equals("Left")) {
				Start.GUI.currentTask.setText("Moving left");
				this.cmdLeft();
				Start.GUI.task = "";
				Start.GUI.currentTask.setText("");
			} else if (cmd.equals("Right")) {
				Start.GUI.currentTask.setText("Moving right");
				this.cmdRight();
				Start.GUI.task = "";
				Start.GUI.currentTask.setText("");
			} else if (cmd.equals("CalibrateNoxBL")) {
				Start.GUI.currentTask.setText("Calibrating Nox to BL");
				this.cmdCalibrateNoxBL();
				Start.GUI.task = "";
				Start.GUI.currentTask.setText("");
			} else if (cmd.equals("Swap Secretary")) {
				this.cmdSwapSecretary();
				Start.GUI.task = "";
			}
			// EAST
			else if (cmd.equals("Update Map List")) {
				this.cmdUpdateMapList();
				Start.GUI.task = "";
			} else if (cmd.equals("Grind Selected Map")) {
				this.cmdGrindSelectedMap();
				Start.GUI.task = "";
			} else if (cmd.equals("Stop Grind")) {
				this.cmdStopGrind();
				Start.GUI.task = "";
			}
			
		}
	}
	
	/*** Command Handling ***/
	
	public void cmdSetDataDir() {
		String newPath = Start.GUI.dataDir.getText();
		Start.dataP.setPath(newPath);
		Start.GUI.dataDir.setText(Start.dataP.getPath());
	}
	
	public void cmdUp() {
		if (this.isGrinding())
			return;
		
		int y = Integer.parseInt(Start.GUI.offsetY.getText());
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addY(OFFSET));
		Start.GUI.offsetY.setText(Integer.toString(y + 1));
	}
	
	public void cmdDown() {
		if (this.isGrinding())
			return;
		
		int y = Integer.parseInt(Start.GUI.offsetY.getText());
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addY(-OFFSET));
		Start.GUI.offsetY.setText(Integer.toString(y - 1));
	}
	
	public void cmdLeft() {
		if (this.isGrinding())
			return;
		
		int x = Integer.parseInt(Start.GUI.offsetX.getText());
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addX(OFFSET));
		Start.GUI.offsetX.setText(Integer.toString(x - 1));
	}
	
	public void cmdRight() {
		if (this.isGrinding())
			return;

		int x = Integer.parseInt(Start.GUI.offsetX.getText());
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addX(-OFFSET));
		Start.GUI.offsetX.setText(Integer.toString(x + 1));
	}
	
	public void cmdCalibrateNoxBL() {
		// don't do anything if grinding
		if (this.isGrinding())
			return;

		Start.rbtM.LBdragS(NOX_INI, NOX_FIN_BL);
	}
	
	public void cmdSwapSecretary() {
		if (this.isGrinding())
			return;

		Start.rbtM.LBclickS(SWAP_SEC);
	}
	
	public void cmdUpdateMapList() {
		if (this.isGrinding())
			return;
		
		Start.dataP.updateMapList();
	}
	
	public void cmdGrindSelectedMap() {
		if (this.isGrinding())
			return;
		
		String selected = Start.GUI.mapSelection.getSelectedItem().toString();
		Graph map = Start.dataP.parse(selected);
		if (map != null) {
			System.out.println("Map parsing was successful.");
			// use OFNB grinder
			if (map.onefleet && map.noboss) {
				this.curGrinder = 1;
				this.OFNBGrinder = new Grinder_OFNB(map);
				this.OFNBGrinder.start();
			} 
			// use TFB grinder
			else if (!map.onefleet && !map.noboss) {
				this.curGrinder = 2;
				this.TFBGrinder = new Grinder_TFB(map);
				this.TFBGrinder.start();
			}
			// grinder unavailable
			else {
				System.out.println("Couldn't find correct grinder.");
			}
		} else {
			System.out.println("Map parsing was unsuccessful.");
		}
	}
	
	public void cmdStopGrind() {
		// don't do anything if not grinding
		if (!this.isGrinding())
			return;
		// else request stop to the selected grinder
		if (this.curGrinder == 0) {
			System.out.println("Unexpected error: curGrinder was not set correctly");
		} else if (this.curGrinder == 1) {
			this.OFNBGrinder.setRunning(false);
		} else if (this.curGrinder == 2) {
			this.TFBGrinder.setRunning(false);
		}
	}
	
	/*** Helper Methods ***/
	
	/**
	 * Checks if a grinder is currently running.
	 * @return
	 */
	private boolean isGrinding() {
		return !Start.GUI.mapSelection.isEnabled();
	}
	
	/**
	 * Helper method for pausing the program.
	 * @param x
	 */
	private void pause(int x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
