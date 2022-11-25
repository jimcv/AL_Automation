package map_grind;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import main.*;
import static main.PositionData.*;

public class Grinder_TFB extends Thread {
	
	// Global data
	private Graph map;
	private boolean running;
	
	public Grinder_TFB(Graph map) {
		
	}
	
	/**
	 * Change running state of the thread.
	 * @param b
	 */
	public void setRunning(boolean b) {
		this.running = b;
	}
	
}
