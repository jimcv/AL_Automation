package main;

public class PositionData {
	// Nox
	public static final XY NOX_INI = new XY(657, 159);			// center of O in NOX (658, 157)
	public static final XY NOX_FIN_BL = new XY(20, 300);		// default calibration with AL(2, 315)
	public static final XY NOX_FIN_BR = new XY(1290, 300);		// if this is used make sure to update AL
	
	// Different origins of AL depending on where Nox window has been calibrated
	public static final XY NOX_AL_ORIGIN = new XY(640, 175);
	public static final XY NOX_AL_ORIGIN_BL = new XY(2, 315);
	public static final XY NOX_AL_ORIGIN_BR = new XY(1272, 315);
	
	// AL is used to calibrate relative positions
	// al is used to get absolute position from relative position
	// typical usage: .minus(AL) to calibrate
	// typical usage: .add(al) to use relative coords
	public static final XY AL = NOX_AL_ORIGIN_BL;					// absolute origin of AL window (right on the border to resize)
	public static XY al = NOX_AL_ORIGIN_BL;
	public static final XY SWAP_SEC = new XY(33, 425).add(al);		// swap secretary
	
	/*** AL Sortie ***/
//	public static final int ENTER_MAP_COLOR = 2171962;			// obsolete, specified in CSV
	public static final XY GO1 = new XY(912, 516).add(al);
	public static final int GO1_COLOR = 14587474;
	public static final XY CLEAR_SUBS = new XY(1122, 468).add(al);
	public static final int CLEAR_SUBS_COLOR = 9210508;
	public static final XY GO2 = new XY(1017, 605).add(al);
	public static final int GO2_COLOR = 14587474;
	
	public static final XY MAP_CENTER = new XY(693, 387).add(al);
//	public static final int FLEET_ARROW_COLOR = 1703787;		// obsolete, specified in CSV
	public static final int YELLOW_ENEMY_COLOR = 16771996;
	public static final int RED_ENEMY_COLOR = 16744836;
	public static final int QMARK_COLOR = 8716279;				// TODO: CSV
	
	public static final XY PAUSE = new XY(1240, 40).add(al);	// pause button in battle
	public static final int PAUSE_COLOR = 16249847;				// RGB color of pause button
//	public static final XY CONFIRM = new XY(1013, 642).add(al);	// confirm button after battle
//	public static final int CONFIRM_COLOR = 12940552;
	public static final XY CONFIRM = new XY(1079, 663).add(al);
	public static final int CONFIRM_COLOR = 16777215;
	
	public static final XY ITEMFOUND = new XY(644, 84).add(al);
	public static final int ITEMFOUND_COLOR = 3225963;
	public static final XY ITEMFOUND_2 = new XY(747, 227).add(al);
	public static final int ITEMFOUND_COLOR_2 = 13565951;
	
	public static final XY POPUP_CROSS = new XY(911, 192).add(al);
	public static final int POPUP_CROSS_COLOR = 12931410;
	public static final XY POPUP_CONFIRM = new XY(561, 494).add(al);
	public static final int POPUP_CONFIRM_COLOR = 3827100;
	public static final XY POPUP_CONFIRM_TEXT = new XY(643, 506).add(al);
	public static final int POPUP_CONFIRM_TEXT_COLOR = 16777215;
	
	public static final XY RETREAT = new XY(759, 686).add(al);
	public static final int RETREAT_COLOR = 13524314;
	public static final XY RETREAT_CROSS = new XY(911, 192).add(al);
	public static final int RETREAT_CROSS_COLOR = 12996946;
	public static final XY RETREAT_CONFIRM = new XY(714, 496).add(al);
	public static final XY RETREAT_CONFIRM_2= new XY(781, 513).add(al);
	public static final int RETREAT_CONFIRM_COLOR = 3761573;
	public static final int RETREAT_CONFIRM_COLOR_2 = 16777215;
	
	/*** END AL Sortie ***/
	
	// Moving Map Offset
	public static final int OFFSET = 100;
	
}
