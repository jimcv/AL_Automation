package main;

public class Start {
	
	// Threads
	public static MainGUI GUI;
	public static Handler h;
	public static MouseLocation mLoc;
	
	// Tools
	public static PixelDetection rbtPD;
	public static RoboticMouse rbtM;
	public static DataParser dataP;

	public static void main(String[] args) {
		GUI = new MainGUI();
		h = new Handler();
		mLoc = new MouseLocation();
		
		rbtPD = new PixelDetection();
		rbtM = new RoboticMouse();
		dataP = new DataParser();
		
		GUI.start();
		h.start();
		mLoc.start();
	}

}
