package map_grind;

import main.XY;

/**
 * Data structure representing a map tile
 * @author Jimmy Sheng
 */
public class Tile {
	public XY center;		// XY of tile center
	public XY scan;			// XY of type indicator
	public XY offset;		// map offset
	public String type;		// specify the type of the tile
							// typically takes the value: "", "mob", "siren", "boss", "qmark"
	
	// priority enabled fieds
	public XY air;
	public int airC;
	public XY main;
	public int mainC;
	
	public Tile() {
		this(new XY(0, 0), new XY(0, 0), new XY(0, 0), "",
				new XY(0, 0), 0, new XY(0, 0), 0);
	}
	
	public Tile(XY center, XY scan, XY offset, String type) {
		this(center, scan, offset, type,
				new XY(0, 0), 0, new XY(0, 0), 0);
	}
	
	public Tile(XY center, XY scan, XY offset, String type,
			XY air, int airC, XY main, int mainC) {
		this.center = center;
		this.scan = scan;
		this.offset = offset;
		this.type = type;
		this.air = air;
		this.airC = airC;
		this.main = main;
		this.mainC = mainC;
	}
	
}
