package main;

import java.awt.*;

/**
 * Thread that tracks the mouse's location infos and update MainGUI accordingly.
 * @author Jimmy Sheng
 */
public class MouseLocation extends Thread {
	
	private PointerInfo myPointer;
	private Point pLocation;
	private XY pos;
	private int colorAtCursor;
	
	private boolean running;
	private boolean liveColor;
	
	public MouseLocation() {
		this.pos = new XY(0, 0);
		this.running = true;
		this.liveColor = false;
	}
	
	public void run() {
		while (running) {
			pause(50);
			// get mouse info
			this.myPointer = MouseInfo.getPointerInfo();
			this.pLocation = this.myPointer.getLocation();
			this.pos.set(this.pLocation.getX(), this.pLocation.getY());
			// update color display
			if (liveColor) {
				this.colorAtCursor = Start.rbtPD.getAlphaRGBColor(pos);
				Start.GUI.colorAtCursor.setText(
						Start.rbtPD.intToHex(this.colorAtCursor) + "; "
						+ (this.colorAtCursor & 0xFFFFFF));
				Start.GUI.colorAtCursor.setBackground(new Color(this.colorAtCursor));
				Start.GUI.colorAtCursor.setForeground(new Color(this.colorAtCursor ^ 0xFFFFFF));
			}
			// update display
			Start.GUI.mouseCoords.setText(this.pos.toString());
		}
	}
	
	public int getX() {
		return this.pos.getX();
	}
	
	public int getY() {
		return this.pos.getY();
	}
	
	public void toggleLiveColor() {
		this.liveColor = !this.liveColor;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void setRunning(boolean b) {
		this.running = b;
	}
	
	private void pause(int x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
