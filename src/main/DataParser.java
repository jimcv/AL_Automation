package main;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

import map_grind.*;
import static main.PositionData.*;

/**
 * Parse the CSV data files and build the graph from it.
 * @author Jimmy Sheng
 */
public class DataParser {
	private String path;				// path to root directory
	
	private BufferedReader rd;			// reader for parsing files
	private Pattern p;					// regex pattern for splitting
	private int curLine;				// line number
	private boolean error;				// flag to check if error occurred during parse
	private boolean endpound;			// end pound flagging
	
	// options for the map to be built
	private Graph map;					// map to be returned for grinding
	private boolean relative;			// # flag to use relative coords
	private boolean onefleet;			// # flag to use one single fleet
	private boolean noboss;				// # flag to not farm boss
	private int cols;					// # flag for num of cols
	private int rows;					// # flag for num of rows
	
	private String name;				// # flag of map's name
	private XY entry;					// # flag of map's entry point
	private int mobFleetBattleCount;	// # flag for num of battles of mob fleet
	
	/**
	 * Create an instance of the parser with default directory.
	 */
	public DataParser() {
		this.path = "C:/IDE/AL_MapData/";
		this.p = Pattern.compile(",");
	}
	
	/**
	 * Reset the parser variables
	 */
	public void reset() {
		this.curLine = 0;
		this.error = false;
		this.endpound = false;
		
		this.map = new Graph();
		this.relative = false;
		this.onefleet = false;
		this.noboss = false;
		this.cols = 0;
		this.rows = 0;
		
		this.name = null;
		this.entry = null;
		this.mobFleetBattleCount = 0;
	}
	
	/**
	 * Parse the given file.
	 * @param filename
	 */
	public Graph parse(String filename) {
		// reset the parser
		this.reset();
		// open file
		try {
			this.rd = new BufferedReader(new FileReader(path + filename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			return null;
		}
		// parse line by line
		try {
			while (true) {
				String line = rd.readLine();
				if (line == null)
					break;
				if (!line.equals("")) {
					this.curLine++;
					this.parseLine(line);
				}
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		// if no error occurred return the graph
		if (!this.error) {
			return this.map;
		} else {
			return null;
		}
	}
	
	/**
	 * Helper method to parse an individual line.
	 * @param line
	 */
	private void parseLine(String line) {
		String[] str = this.p.split(line);
		// filter empty lines and empty tags
		if (str.length < 1) {
			return;
		} else if (str[0].length() < 1) {
			return;
		}
		// check flag
		if (str[0].equals("//")) {
			return;
		} else if (str[0].charAt(0) == '#') {
			this.parsePound(str);
		} else if (str[0].charAt(0) == '@') {
			this.parseAt(str);
		}
	}
	
	/**
	 * Helper method to parse a # line
	 * @param line
	 */
	private void parsePound(String[] str) {
		String substr = str[0].substring(1);
		if (this.endpound) {
			this.gotError("end# has already been called.");
			return;
		}
		// check pound
		if (substr.equals("relative")) {
			this.relative = true;
		} else if (substr.equals("onefleet")) {
			this.onefleet = true;
			this.map.onefleet = this.onefleet;
		} else if (substr.equals("noboss")) {
			this.noboss = true;
			this.map.noboss = this.noboss;
		} else if (substr.equals("cols")) {
			try {
				this.cols = Integer.parseInt(str[1]);
				if (this.cols <= 0) {
					throw new NumberFormatException();
				} else if (this.rows != 0) {
					this.createGraph();
				}
			} catch (NumberFormatException e) {
				this.gotError("Unvalid integer expression.");
			}
		} else if (substr.equals("rows")) {
			try {
				this.rows = Integer.parseInt(str[1]);
				if (this.rows <= 0) {
					throw new NumberFormatException();
				} else if (this.cols != 0) {
					this.createGraph();
				}
			} catch (NumberFormatException e) {
				this.gotError("Unvalid integer expression.");
			}
		} else if (substr.equals("name")) { 
			if (this.name != null) {
				this.gotError("Name has already been set.");
			} else {
				this.setName(str);
			}
		} else if (substr.equals("mobfleetbattlecount")) {
			if (this.map != null) {
				this.setMobFleetBattleCount(str);
			} else {
				this.gotError("Map not initialized before specifiying mobfleetbattlecount.");
			}
		} else if (substr.equals("entry")) {
			if (this.entry != null) {
				this.gotError("Entry point has already been specified.");
			} else {
				this.setEntryPoint(str);
			}
		} else if (substr.equals("obstacle")) {
			if (this.map != null) {
				this.remObstacle(str);
			} else {
				this.gotError("Map not initialized before specifiying obstacles.");
			}
		} else if (substr.equals("start")) {
			if (this.map != null) {
				this.addStartPosition(str);
			} else {
				this.gotError("Map not initialized before specifiying start positions.");
			}
		} else if (substr.equals("initialoffset")) {
			if (this.map != null) {
				this.setInitialOffset(str);
			} else {
				this.gotError("Map not initialized before specifying initialoffset.");
			}
		} else if (substr.equals("priority")) {
			if (this.map != null) {
				this.addPriority(str);
			} else {
				this.gotError("Map not initialized before specifying priority.");
			}
		} else if (substr.equals("end#")) {
			this.endpound = true;
			// check if name and entry point have been set
			if (this.name == null || this.entry == null) {
				this.gotError("#name or #entry have not been set.");
			}
			// check necessary OFNB params
			if (this.onefleet && this.noboss) {
				if (this.mobFleetBattleCount == 0) {
					this.gotError("#mobfleetbattlecount have not been set.");
				}
			}
			// check if cols and rows have been specified
			if (this.map != null) {
				this.drawEdges();
			} else {
				this.gotError("Map not initialized before ending #.");
			}
		}
		// end pound
	}
	
	/**
	 * Helper method to parse an @ line
	 * @param line
	 */
	private void parseAt(String[] str) {
		if (!this.endpound) {
			this.gotError("end# has not yet been called.");
			return;
		}
		/*** One fleet parse ***/
		if (this.onefleet) {
			this.parseAtOF(str);
		} 
		/*** End one fleet parse ***/
		else {
			this.gotError("#onefleet not specified. Multiple fleets currently not supported.");
			return;
		}
	}
	
	/**
	 * Helper method to parse onefleet @ line
	 * @param str
	 */
	private void parseAtOF(String[] str) {
		// minimum num of args is 9
		if (str.length < 9) {
			this.gotError("Not enough arguments for @.");
			return;
		}
		// get position
		XY pos = this.parseIndex(str[3]);
		// parse coordinates
		try {
			int x = Integer.parseInt(str[1]);
			int y = Integer.parseInt(str[2]);
			int scanX = Integer.parseInt(str[4]);
			int scanY = Integer.parseInt(str[5]);
			int offsetX = Integer.parseInt(str[6]);
			int offsetY = Integer.parseInt(str[7]);
			String type = str[8];
			if (x < 0 || y < 0 || scanX < 0 || scanY < 0 || offsetX < 0 || offsetY < 0) {
				throw new NumberFormatException();
			} else {
				XY center = new XY(x, y);
				XY scan = new XY(scanX, scanY);
				XY offset = new XY(offsetX, offsetY);
				// check use relative
				if (relative) {
					center = center.add(al);
					scan = scan.add(al);
				}
				Tile newTile = new Tile(center, scan, offset, type);
				this.map.setTile(pos, newTile);
				
				/** One fleet priority parse **/
				if (!this.map.prio.isEmpty()) {
					this.parseAtOFPriority(str, newTile);
				}
				/** End one fleet priority parse **/
			}
		} catch (NumberFormatException e) {
			this.gotError("Invalid coordinate expression.");
		}
	}
	
	/**
	 * Helper method to parse onefleet priority enabled @ line
	 * @param str
	 */
	private void parseAtOFPriority(String[] str, Tile t) {
		// need 15 args, otherwise simply return and leave default values
		if (str.length < 15) {
			return;
		}
		// parse
		try {
			// array structure: airX, airY, mainX, mainY
			int[] data = new int[4];
			data[0] = Integer.parseInt(str[9]);
			data[1] = Integer.parseInt(str[10]);
			int airC = Integer.parseInt(str[11]);
			data[2] = Integer.parseInt(str[12]);
			data[3] = Integer.parseInt(str[13]);
			int mainC = Integer.parseInt(str[14]);
			// check coordinates
			for (int i = 0; i < data.length; i++) {
				if (data[i] < 0) {
					throw new NumberFormatException();
				}
			}
			XY air = new XY(data[0], data[1]);
			XY main = new XY(data[2], data[3]);
			// check relative
			if (this.relative) {
				// ignore (0, 0) entries
				if (!air.equals(XY.O)) {
					air = air.add(al);
				}
				if (!main.equals(XY.O)) {
					main = main.add(al);
				}
			}
			// set
			t.air = air;
			t.main = main;
			t.airC = airC;
			t.mainC = mainC;
		} catch (NumberFormatException e) {
			this.gotError("Invalid priority @ line parameters. (Syntax: airX, airY, airC, mainX, mainY, mainC)");
		}
	}
	
	/**
	 * Initialize the graph object. Called upon both cols and rows are set.
	 */
	private void createGraph() {
		for (int i = 1; i <= this.cols; i++) {
			for (int j = 1; j <= this.rows; j++) {
				Tile newTile = new Tile();
				this.map.addVertex(new XY(i, j), newTile);
			}
		}
	}
	
	/**
	 * Add edges to the graph. Called only upon end# and map is not null.
	 */
	private void drawEdges() {
		for (int i = 1; i <= this.cols; i++) {
			for (int j = 1; j <= this.rows; j++) {
				XY pos = new XY(i, j);
				// add edge to adjacent coordinates if these vertices exist
				this.map.addEdge(pos, pos.addX(1));
				this.map.addEdge(pos, pos.minusX(1));
				this.map.addEdge(pos, pos.addY(1));
				this.map.addEdge(pos, pos.minusY(1));
			}
		}
	}
	
	private void setName(String[] str) {
		// need 2 args 
		if (str.length < 2) {
			this.gotError("Not enough arguments for #name.");
			return;
		}
		this.name = str[1];
		this.map.name = this.name;
	}
	
	private void setMobFleetBattleCount(String[] str) {
		// need 2 args
		if (str.length < 2) {
			this.gotError("Not enough arguments for #mobfleetbattlecount.");
			return;
		}
		try {
			// parse
			this.mobFleetBattleCount = Integer.parseInt(str[1]);
			if (this.mobFleetBattleCount <= 0) {
				throw new NumberFormatException();
			}
			this.map.mobFleetBattleCount = this.mobFleetBattleCount;
		} catch(NumberFormatException e) {
			this.gotError("Invalid integer expression.");
		}
	}
	
	private void setEntryPoint(String[] str) {
		// need 4 args
		if (str.length < 4) {
			this.gotError("Not enough arguments for #entry.");
			return;
		}
		try {
			// parse
			int x = Integer.parseInt(str[1]);
			int y = Integer.parseInt(str[2]);
			int c = Integer.parseInt(str[3]);
			if (x < 0 || y < 0) {
				throw new NumberFormatException();
			}
			XY pos = new XY(x, y);
			// check relative
			if (this.relative) {
				pos = pos.add(al);
			}
			// set
			this.entry = pos;
			this.map.entry = this.entry;
			this.map.entry_color = c;
		} catch (NumberFormatException e) {
			this.gotError("Invalid integer expression.");
		}
	}
	
	/**
	 * Remove obstacle vertices from the graph.
	 * @param str
	 */
	private void remObstacle(String[] str) {
		if (str.length <= 1) {
			return;
		}
		for (int n = 1; n < str.length; n++) {
			this.map.removeVertex(this.parseIndex(str[n]));
		}
	}
	
	/**
	 * Add the given start position to the graph.
	 * @param str
	 */
	private void addStartPosition(String[] str) {
		// minimum num of args is 5
		if (str.length < 5) {
			this.gotError("Not enough arguments for #start.");
			return;
		}
		// parse coords
		XY pos = this.parseIndex(str[1]);
		try {
			int x = Integer.parseInt(str[2]);
			int y = Integer.parseInt(str[3]);
			if (x < 0 || y < 0) {
				throw new NumberFormatException();
			} else {
				Tile newTile = new Tile();
				newTile.scan = new XY(x, y);
				// check if use relative
				if (this.relative) {
					newTile.scan = newTile.scan.add(al);
				}
				this.map.startPositions.put(pos, newTile);
			}
		} catch (NumberFormatException e) {
			this.gotError("Invalid coordinate expression.");
		}
		// parse color
		try {
			int c = Integer.parseInt(str[4]);
			this.map.startColors.put(pos, c);
		} catch (NumberFormatException e) {
			this.gotError("Invalid integer RGB color.");
		}
	}
	
	/**
	 * Modify the initialOffset field of the map.
	 * @param str
	 */
	private void setInitialOffset(String[] str) {
		// needs 3 args
		if (str.length < 3) {
			this.gotError("Not enough arguments for #initialoffset.");
			return;
		}
		// parse
		try {
			int x = Integer.parseInt(str[1]);
			int y = Integer.parseInt(str[2]);
			if (x < 0 || y < 0) {
				throw new NumberFormatException();
			}
			// set
			this.map.initialOffset.cpy(new XY(x, y));
		} catch (NumberFormatException e) {
			this.gotError("Invalid coordinate expression.");
		}
	}
	
	/**
	 * Add a priority parameter to the HashMap prio in the map.
	 * @param str
	 */
	private void addPriority(String[] str) {
		// needs 3 args
		if (str.length < 3) {
			this.gotError("Not enough arguments for #priority.");
			return;
		}
		// parse
		try {
			String name = str[1];
			int val = Integer.parseInt(str[2]);
			// put
			this.map.prio.put(name, val);
		} catch (NumberFormatException e) {
			this.gotError("Invalid arguments for #priority. (Syntax: str_name,int_value)");
		}
	}
	
	/**
	 * Print an error message and set error flag to true.
	 * @param reason
	 */
	private void gotError(String reason) {
		this.error = true;
		System.out.println("Parse error at line " + this.curLine + ": " + reason);
	}
	
	/**
	 * Reset path to default.
	 */
	public void setPath() {
		this.setPath("C:/IDE/AL_MapData/");
	}
	
	/**
	 * Change path to the given location.
	 * @param path
	 * @return
	 */
	public void setPath(String path) {
		if (path == null) {
			return;
		} else if (path.equals("")) {
			this.setPath();
		} else if (path.charAt(path.length() - 1) != '/') {
			this.path = path + "/";
		} else {
			this.path = path;
		}
	}
	
	/**
	 * Return the current path.
	 * @return
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Update the map selection drop-down menu
	 */
	public void updateMapList() {
		Start.GUI.mapSelection.removeAllItems();
		File folder = new File(this.path);
		// get only .csv files
		FilenameFilter filterCSV = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		};
		// apply filter
		String[] files = folder.list(filterCSV);
		if (files == null) {
			System.out.println("Invalid Directory!");
			return;
		} else {
			Arrays.sort(files);
			// update drop-down menu
			for (int i = 0; i < files.length; i++) {
				Start.GUI.mapSelection.addItem(files[i]);
			}
		}
	}
	
	/**
	 * Convert given map location to XY address to enable arthmetic operations
	 * @param c
	 * @return
	 */
	public XY parseIndex(String address) {
		if (address.length() < 2) {
			this.gotError("Invalid address " + address);
			return new XY(0, 0);
		} else {
			// parse column
			char c = address.charAt(0);
			int x = c - 'A' + 1;
			// parse row
			String str = address.substring(1);
			try {
				int y = Integer.parseInt(str);
				return new XY(x, y);
			} catch (NumberFormatException e) {
				this.gotError("Invalid address " + address);
				return new XY(0, 0);
			}
		}
	}
	
}
