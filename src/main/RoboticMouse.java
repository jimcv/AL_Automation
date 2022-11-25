package main;

import java.awt.*;
import java.awt.event.InputEvent;

public class RoboticMouse extends Thread {
	private Robot rbt;	// robot object
	private PointerInfo prevPointer;	// for marking previous mouse position
	private Point prevLocation;
	private XY prev;
	
	/**
	 * Initialize robot and XY field.
	 */
	public RoboticMouse() {
		try {
			this.rbt = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		this.prev = new XY(0, 0);
	}
	
	/*** Utilities ***/
	
	public void moveTo(XY pos) {
		this.rbt.mouseMove(pos.getX(), pos.getY());
	}
	
	public void moveBack() {
		this.moveTo(this.prev);
	}
	
	public void LBclick(XY pos) {
		this.moveTo(pos);
		this.rbt.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.rbt.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public void LBclickS(XY pos) {
		this.setPrevLocation();
		this.LBclick(pos);
		this.moveTo(this.prev);
	}
	
	public void LBdrag(XY pos0, XY pos1) {
		this.moveTo(pos0);
		this.rbt.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		pause(500);
		this.mouseGlide(pos0, pos1);
		this.rbt.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public void LBdragS(XY pos0, XY pos1) {
		this.setPrevLocation();
		this.LBdrag(pos0, pos1);
		this.moveTo(this.prev);
	}
	
	public void mouseGlide(XY pos0, XY pos1) {
		int x1 = pos0.getX();
		int y1 = pos0.getY();
		int n = 100;	// number of drag steps
		int t = 1000;	// time interval in ms
		double dx = XY.getDx(pos0, pos1) / ((double) n);
		double dy = XY.getDy(pos0, pos1) / ((double) n);
		double dt = t / ((double) n);
		for (int step = 1; step <= n; step++) {
			pause((int) dt);
			this.rbt.mouseMove((int) (x1 + dx * step), (int) (y1 + dy * step));
		}
	}
	
	/*** Helper Methods ***/
	
	private void pause(int x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void setPrevLocation() {
		this.prevPointer = MouseInfo.getPointerInfo();
		this.prevLocation = prevPointer.getLocation();
		this.prev.set(prevLocation.getX(), prevLocation.getY());
	}
	
}
