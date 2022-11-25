package map_grind;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import main.*;
import static main.PositionData.*;

/**
 * Grind AI for #onefleet, #noboss maps.
 * @author Jimmy Sheng
 */
public class Grinder_OFNB extends Thread {
	
	// Global data
	private Graph map;
	private boolean running;
	
	private String name;
	private int battleCount;				// mobFleetBattleCount
	private XY entry;
	private int entry_color;
	
	// Temporary data to be reset each loop
	private XY curPos;			// current position of the fleet
	private XY curOffset;		// current offset
	private boolean foundEnemy;
	private boolean inBattle;
	
	/**
	 * Constructor.
	 * @param map
	 */
	public Grinder_OFNB(Graph map) {
		this.map = map;
		this.running = false;
		
		this.name = map.name;
		this.battleCount = map.mobFleetBattleCount;
		this.entry = map.entry;
		this.entry_color = map.entry_color;
		
		this.reset();
	}
	
	private void reset() {
		this.curPos = new XY(0, 0);
		this.curOffset = new XY(0, 0);
		this.foundEnemy = false;
		this.inBattle = false;
	}
	
	/**
	 * Entry point.
	 */
	public void run() {
		// update GUI
		Start.GUI.currentTask.setText("Grinding: " + this.name);
		Start.GUI.currentTask.setForeground(Color.RED);
		Start.GUI.mapSelection.setEnabled(false);
		System.out.println("Map contains " + this.map.vertexList.size() + " nodes.");
		System.out.println("Mob fleet will attack " + this.battleCount + " times.");
		this.setRunning(true);
		// main loop
		while (this.running) {
			// reset temporary data
			this.reset();
			
			// enter the map
			if (this.running) {
				this.enterMap(this.entry, this.entry_color);
				pause(5000);
			}
			
			// scan popup
			if (this.running && this.scanPopup()) {
				pause(1000);
			}
			
			// ensure initial offset
			if (this.running) {
				this.ensureOffset(this.map.initialOffset);
			}
			
			// scan starting position
			if (this.running) {
				this.setRunning(this.scanStartPosition());
				pause(1000);
			}
			
			// run attack loop mobFleetBattleCount times
			for (int i = 0; i < this.battleCount; i++) {
				// passively go to qmark if not obstructed
				if (this.running) {
					this.qMarkPassive();
					pause(1000);
				}
				
				// TODO: for now, simply attack nearest enemies.
				// UPDATE: now enable priority detection
				if (this.running) {
					if (this.map.prio.isEmpty()) {
						this.attackNearest();
						
					} else {
						this.attackNearestPriority();
					}
					pause(10000);
				}
				
				
				// if found enemy, scan pause button
				if (this.running && this.foundEnemy) {
					this.scanPauseButton();
					pause(6000);
				}
				
				// scan popup
				if (this.running && this.scanPopup()) {
					pause(1000);
				}
			}
			
			// (obsolete) blindly go to qmarks
//			if (this.running) {
//				this.qMarkBlind();
//				pause(1000);
//			}
			
			// (new) use qMarkPassive() instead of qMarkBlind()
			if (this.running) {
				this.qMarkPassive();
				pause(1000);
			}
			
			// retreat
			if (this.running) {
				this.retreat();
				pause(5000);
			}
			
		}
		// task finished, update GUI text
		System.out.println("Grind Stopped.");
		Start.GUI.currentTask.setText("");
		Start.GUI.currentTask.setForeground(Color.BLACK);
		Start.GUI.mapSelection.setEnabled(true);
	}
	
	/*** Grind AI ***/
	
	/**
	 * Enter the given map without submarine.
	 */
	private void enterMap(XY enter, int color) {
		// press map
		if (Start.rbtPD.scanS(enter, color)) {
			System.out.println("-> Entering map.");
			Start.rbtM.LBclickS(enter);
			pause(1000);
			// press GO1
			if (Start.rbtPD.scanS(GO1, GO1_COLOR)) {
				Start.rbtM.LBclickS(GO1);
				pause(1000);
				// press clear submarine
				if (Start.rbtPD.scanS(CLEAR_SUBS, CLEAR_SUBS_COLOR)) {
					Start.rbtM.LBclickS(CLEAR_SUBS);
					pause(1000);
				}
				// press GO2
				if (Start.rbtPD.scanS(GO2, GO2_COLOR)) {
					Start.rbtM.LBclickS(GO2);
				}
			}
		}
	}
	
	/**
	 * Scan starting position of the fleet and set curPos accordingly.
	 */
	private boolean scanStartPosition() {
		for (XY pos : this.map.startPositions.keySet()) {
			XY scan = this.map.startPositions.get(pos).scan;
			int c = this.map.startColors.get(pos);
			if (Start.rbtPD.scanS(scan, c)) {
				System.out.println("-> Fleet starting at " + this.toMapCoord(pos) + ".");
				this.curPos.cpy(pos);
				return true;
			}
		}
		System.out.println("ERROR: could not determine starting position.");
		return false;
	}
	
	/**
	 * If current offset is not equal to the given offset, drag the map so that they are equal.
	 * @param offset
	 */
	private void ensureOffset(XY offset) {
		System.out.println("Current offset: " + this.curOffset);
		if (!this.curOffset.equals(offset)) {
			int toMoveX = offset.getX() - this.curOffset.getX();
			int toMoveY = offset.getY() - this.curOffset.getY();
			while (toMoveX > 0) {
				this.dragR();
				toMoveX--;
				pause(500);
			}
			while (toMoveX < 0) {
				this.dragL();
				toMoveX++;
				pause(500);
			}
			while (toMoveY > 0) {
				this.dragU();
				toMoveY--;
				pause(500);
			}
			while (toMoveY < 0) {
				this.dragD();
				toMoveY++;
				pause(500);
			}
		}
		System.out.println("New current offset: " + this.curOffset);
	}
	
	/**
	 * Execute x - 1 drag
	 */
	private void dragL() {
		// drag
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addX(OFFSET));
		this.curOffset = this.curOffset.addX(-1);
		// update GUI text
		int x = Integer.parseInt(Start.GUI.offsetX.getText());
		Start.GUI.offsetX.setText(Integer.toString(x - 1));
	}
	
	/**
	 * Execute x + 1 drag
	 */
	private void dragR() {
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addX(-OFFSET));
		this.curOffset = this.curOffset.addX(1);
		int x = Integer.parseInt(Start.GUI.offsetX.getText());
		Start.GUI.offsetX.setText(Integer.toString(x + 1));
	}
	
	/**
	 * Execute y - 1 drag
	 */
	private void dragD() {
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addY(-OFFSET));
		this.curOffset = this.curOffset.addY(-1);
		int y = Integer.parseInt(Start.GUI.offsetY.getText());
		Start.GUI.offsetY.setText(Integer.toString(y - 1));
	}
	
	/**
	 * Execute y + 1 drag
	 */
	private void dragU() {
		Start.rbtM.LBdragS(MAP_CENTER, MAP_CENTER.addY(OFFSET));
		this.curOffset = this.curOffset.addY(1);
		int y = Integer.parseInt(Start.GUI.offsetY.getText());
		Start.GUI.offsetY.setText(Integer.toString(y + 1));
	}
	
	
	
	/**
	 * Passively go to qmark if there's a path to it.
	 */
	private void qMarkPassive() {
		this.map.resetVisited();
		Queue<XY> reachableQMarks = new LinkedList<XY>();
		Queue<XY> q = new LinkedList<XY>();
		q.add(this.curPos);
		this.map.setVisited(this.curPos, true);
		while (!q.isEmpty()) {
			XY cur = q.remove();
			// ignore the root (position where ally fleet is currently on)
			if (!cur.equals(this.curPos)) {
				Tile t = this.map.getTile(cur);
				// null check
				if (t != null) {
					// get qmark
					if (t.type.equals("qmark")) {
						if (Start.rbtPD.scanS(t.scan, QMARK_COLOR)) {
							// add cur to reachable
							reachableQMarks.add(cur);
						}
					}
				}
			}
			// add next vertices
			for (XY adj : this.map.getLinks(cur)) {
				if (!this.map.getVisited(adj)) {
					Tile t = this.map.getTile(adj);
					// TODO: ignore offsets for now
					if (t.offset.equals(this.curOffset)) {
						// ignore tile if it's obstructed by an enemy
						if (!this.scanMob(adj)) {
							q.add(adj);
						}
						this.map.setVisited(adj, true);
					}
				}
			}
		}
		// goto all reachable qmarks
		while (!reachableQMarks.isEmpty()) {
			XY pos = reachableQMarks.remove();
			Tile t = this.map.getTile(pos);
			System.out.println("-> Getting qmark at " + this.toMapCoord(pos) + ".");
			Start.rbtM.LBclickS(t.center);
			pause(8000);
			this.scanItemfound();
			this.curPos.cpy(pos);
			pause(1000);
		}
	}
	
	/**
	 * Simply clicks on the nearest detected enemy using breadth first traversal.
	 * Temporary option before implementing a more sophisticated system.
	 */
	private void attackNearest() {
		this.foundEnemy = false;
		this.map.resetVisited();
		Queue<XY> q = new LinkedList<XY>();
		q.add(this.curPos);
		this.map.setVisited(this.curPos, true);
		while (!q.isEmpty()) {
			XY cur = q.remove();
			// ignore the root (position where ally fleet is currently on)
			if (!cur.equals(this.curPos)) {
				// TODO: depending on t.type, do different things
				if (this.scanMob(cur)) {
					Tile t = this.map.getTile(cur);
					System.out.println("-> Attacking enemy at " + this.toMapCoord(cur) + ".");
					this.foundEnemy = true;
					Start.rbtM.LBclickS(t.center);
					// update curPos
					this.curPos.cpy(cur);
					break;
				}
			}
			// add next vertices
			for (XY adj : this.map.getLinks(cur)) {
				if (!this.map.getVisited(adj)) {
					// TODO: ignore offsets for now
					if (this.map.getTile(adj).offset.equals(this.curOffset)) {
						q.add(adj);
						this.map.setVisited(adj, true);
					}
				}
			}
		}
		// stop if no enemy has been found
		if (!this.foundEnemy) {
			System.out.println("ERROR: failed to scan enemy.");
			this.running = false;
		}
	}
	
	/**
	 * Similar to attackNearest, but also enable priority checking.
	 */
	private void attackNearestPriority() {
		// setup
		this.foundEnemy = false;
		XY highestPriorityTarget = new XY(0, 0);
		int highestPriority = Integer.MIN_VALUE;
		// traversal
		this.map.resetVisited();
		Queue<XY> q = new LinkedList<XY>();
		q.add(this.curPos);
		this.map.setVisited(this.curPos, true);
		while (!q.isEmpty()) {
			XY cur = q.remove();
			// ignore the root (position where ally fleet is currently on)
			if (!cur.equals(this.curPos)) {
				Integer priority = this.scanMobPriority(cur);
				if (priority != null && priority > highestPriority) {
					this.foundEnemy = true;
					highestPriorityTarget.cpy(cur);
					highestPriority = priority;
				}
				// add next vertices ONLY IF cur is not a mob fleet
				else if (priority == null) {
					for (XY adj : this.map.getLinks(cur)) {
						if (!this.map.getVisited(adj)) {
							// TODO: ignore offsets for now
							if (this.map.getTile(adj).offset.equals(this.curOffset)) {
								q.add(adj);
								this.map.setVisited(adj, true);
							}
						}
					}
				}
				// do nothing if there's a mob fleet with lower priority
			}
			// if cur is where ally fleet is, simply add next vertices
			else {
				for (XY adj : this.map.getLinks(cur)) {
					if (!this.map.getVisited(adj)) {
						// TODO: ignore offsets for now
						if (this.map.getTile(adj).offset.equals(this.curOffset)) {
							q.add(adj);
							this.map.setVisited(adj, true);
						}
					}
				}
			}
		}
		// attack enemy
		if (this.foundEnemy) {
			Tile t = this.map.getTile(highestPriorityTarget);
			System.out.println("-> Attacking enemy at " + this.toMapCoord(highestPriorityTarget) + ".");
			Start.rbtM.LBclickS(t.center);
			this.curPos.cpy(highestPriorityTarget);
		}
		// stop if no enemy has been found
		else {
			System.out.println("ERROR: failed to scan enemy.");
			this.running = false;
		}
	}
	
	private void scanPauseButton() {
		int attempts = 1;
		while (true) {
			if (Start.rbtPD.scanS(PAUSE, PAUSE_COLOR)) {
				System.out.println("-> Battle started.");
				this.inBattle = true;
				break;
			}
			// check qmark pop up, and click on target again
			if (this.scanItemfound()) {
				pause(1000);
				// click on target again
				Start.rbtM.LBclickS(this.map.getTile(this.curPos).center);
			}
			// scan popup
			if (this.scanPopup()) {
				pause(1000);
			}
			// repeatedly click on target after 16s
			if (attempts >= 6) {
				Start.rbtM.LBclickS(this.map.getTile(this.curPos).center);
			}
			// terminate after 30s
			if (attempts >= 15) {
				System.out.println("ERROR: failed to scan pause button.");
				this.running = false;
				break;
			}
			// increments
			attempts++;
			pause(2000);
		}
		
		// is in battle, call endBattle when pause button disappears
		if (this.inBattle) {
			while (this.inBattle) {
				pause(2000);
				// pause button disappeared
				if (!Start.rbtPD.scanS(PAUSE, PAUSE_COLOR)) {
					pause(2000);
					// double check pause button
					if (!Start.rbtPD.scanS(PAUSE, PAUSE_COLOR)) {
						this.inBattle = false;
					}
				}
			}
			this.endBattle();
		}
	}
	
	private void endBattle() {
		System.out.println("-> Battle ended.");
		boolean confirmPressed = false;
		// scan and click
		while (!confirmPressed) {
			pause(2500);
			confirmPressed = Start.rbtPD.scanS(CONFIRM, CONFIRM_COLOR);
			Start.rbtM.LBclickS(CONFIRM);
		}
		// double check
		pause(2000);
		if (Start.rbtPD.scanS(CONFIRM, CONFIRM_COLOR)) {
			Start.rbtM.LBclickS(CONFIRM);
		}
	}
	
	/**
	 * Returns true if there's a mob fleet at the given pos. False otherwise.
	 * @param pos
	 * @return
	 */
	private boolean scanMob(XY pos) {
		Tile t = this.map.getTile(pos);
		if (t != null) {
			if (t.type.equals("mob")) {
				if (Start.rbtPD.scanB(t.scan, YELLOW_ENEMY_COLOR)
						|| Start.rbtPD.scanB(t.scan, RED_ENEMY_COLOR)) {
					System.out.println("-> Detected enemy at " + this.toMapCoord(pos) + ".");
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the priority of the mob fleet at the given pos, null if no mob fleet is present.
	 * Can only be used when priority is enabled.
	 * @param pos
	 * @return
	 */
	private Integer scanMobPriority(XY pos) {
		if (this.map.prio.isEmpty()) {
			return null;
		}
		Tile t = this.map.getTile(pos);
		if (t != null) {
			if (t.type.equals("mob")) {
				int priority = 0;
				boolean detected = false;
				String info = "";
				// detect enemy
				if (Start.rbtPD.scanB(t.scan, YELLOW_ENEMY_COLOR)) {
					detected = true;
					info += "small/medium ";
					if (this.map.prio.containsKey("yellow")) {
						priority += this.map.prio.get("yellow");
					}
				} else if (Start.rbtPD.scanB(t.scan, RED_ENEMY_COLOR)) {
					detected = true;
					info += "large ";
					if (this.map.prio.containsKey("red")) {
						priority += this.map.prio.get("red");
					}
				}
				// scan type
				if (detected) {
					// ignore (0, 0)
					boolean scanAir = !t.air.equals(XY.O);
					boolean scanMain = !t.main.equals(XY.O);
					// scan
					if (scanAir && Start.rbtPD.scanS(t.air, t.airC)) {
						info += "carrier fleet ";
						// add priority
						if (this.map.prio.containsKey("mob_air")) {
							priority += this.map.prio.get("mob_air");
						}
					} else if (scanMain && Start.rbtPD.scanS(t.main, t.mainC)) {
						info += "main fleet ";
						// add priority
						if (this.map.prio.containsKey("mob_main")) {
							priority += this.map.prio.get("mob_main");
						}
					}
					// everything else is considered recon fleet
					else {
						info += "recon fleet ";
						// add priority
						if (this.map.prio.containsKey("mob_recon")) {
							priority += this.map.prio.get("mob_recon");
						}
					}
					
					// announce result
					System.out.println("-> Detected " + info + "with priority " + priority + " at " + this.toMapCoord(pos) + ".");
					return priority;
				}
			}
		}
		return null;
	}
	
	private boolean scanPopup() {
		if (Start.rbtPD.scanS(POPUP_CONFIRM, POPUP_CONFIRM_COLOR)) {
			if (Start.rbtPD.scanS(POPUP_CONFIRM_TEXT, POPUP_CONFIRM_TEXT_COLOR)) {
				System.out.println("-> Popup detected.");
				Start.rbtM.LBclickS(POPUP_CROSS);
				return true;
			}
		}
		return false;
	}
	
	private boolean scanItemfound() {
		if (Start.rbtPD.scanS(ITEMFOUND, ITEMFOUND_COLOR)) {
			Start.rbtM.LBclickS(ITEMFOUND);
			return true;
		} else if (Start.rbtPD.scanS(ITEMFOUND_2, ITEMFOUND_COLOR_2)) {
			Start.rbtM.LBclickS(ITEMFOUND);
			return true;
		}
		return false;
	}
	
	
	
	
	
	/**
	 * Breadth first traversal starting at the given position.
	 * @param root
	 */
	private void bft(XY root) {
		this.map.resetVisited();
		Queue<XY> q = new LinkedList<XY>();
		q.add(root);
		this.map.setVisited(root, true);
		while (!q.isEmpty()) {
			XY cur = q.remove();
			// TODO: visit vertex at cur
			// ignore the root (position where ally fleet is currently on)
			if (!cur.equals(root)) {
				
				
				
				
			}
			// add next vertices
			for (XY adj : this.map.getLinks(cur)) {
				if (!this.map.getVisited(adj)) {
					q.add(adj);
					this.map.setVisited(adj, true);
				}
			}
		}
	}
	
	/**
	 * (obsolete) Blindly go to all qmark locations
	 */
	private void qMarkBlind() {
		for (XY pos : this.map.vertexList.keySet()) {
			Tile t = this.map.getTile(pos);
			if (t.type.equals("qmark")) {
				if (Start.rbtPD.scanS(t.scan, QMARK_COLOR)) {
					System.out.println("-> Attempting to get qmark at " + this.toMapCoord(pos) + ".");
					Start.rbtM.LBclickS(t.center);
					pause(8000);
					this.scanItemfound();
					pause(1000);
				}
			}
		}
	}
	
	/**
	 * Retreat from the current map.
	 */
	private void retreat() {
		// press retreat
		if (Start.rbtPD.scanS(RETREAT, RETREAT_COLOR)) {
			System.out.println("-> Retreat.");
			Start.rbtM.LBclickS(RETREAT);
			pause(1000);
			// press confirm
			if (Start.rbtPD.scanS(RETREAT_CONFIRM, RETREAT_CONFIRM_COLOR)) {
				Start.rbtM.LBclickS(RETREAT_CONFIRM);
			} 
			// double check displaced popup
			else {
				if (Start.rbtPD.scanS(RETREAT_CONFIRM_2, RETREAT_CONFIRM_COLOR_2)) {
					Start.rbtM.LBclickS(RETREAT_CONFIRM_2);
				}
			}
		}
	}
		
	/**
	 * Change running state of the thread.
	 * @param b
	 */
	public void setRunning(boolean b) {
		this.running = b;
	}
	
	/*** Helper Methods ***/
	
	/**
	 * Helper method for delays.
	 * @param x
	 */
	private void pause(int x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert XY to readable AL map coord.
	 * @param pos
	 * @return
	 */
	private String toMapCoord(XY pos) {
		char x = (char)(pos.getX() - 1 + 'A');
		return "" + x + pos.getY();
	}
	
}
