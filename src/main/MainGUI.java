package main;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

import static main.PositionData.*;

/**
 * Main GUI of the program
 * @author Jimmy Sheng
 */
public class MainGUI extends Thread implements ActionListener {
	// main window properties
	private JFrame mainFrame;				// main window frame
	private Dimension mainMinSize;			// main window minimum size
	
	// main window contents
	private JPanel northPanel;				// JPanel for NORTH
	private JPanel centerPanel;				// JPanel for CENTER
	private JPanel southPanel;				// JPanel for SOUTH
	private JPanel eastPanel;				// JPanel for EAST
	private JPanel westPanel;				// JPanel for WEST
	private JPanel[] centerSubPanels;		// SubPanels for CENTER
	private JPanel[] eastSubPanels;			// SubPanels for EAST
	private JPanel[] westSubPanels;			// SubPanels for WEST
	private JPanel southSubPanel1;			// SubPanel1 for SOUTH
	
	// NORTH
	JTextField dataDir;						// text field to input directory for data
	// CENTER
	JLabel mouseCoords;						// updated mouse coordinates
	JTextField colorAtCursor;				// updated color at cursor
	public JTextField offsetX;				// updated current map offset
	public JTextField offsetY;
	private JLabel fleet;					// updated current fleet
	public JTextField currentTask;			// updated current task display
	// WEST
	private JTextField moveMouseToX;		// user input movemouse
	private JTextField moveMouseToY;
	private JRadioButton useRelative;		// toggle relative mouse move
	// EAST
	public JComboBox<String> mapSelection;	// drop-down menu to select map
	
	// main window controls
	String task = "";						// control param for handler
	
	// constants definition

	/**
	 * Create a window with the default name.
	 */
	public MainGUI() {
		this("Haru's AL automation tools v1.3");
	}

	/**
	 * Create a 1000x800 window with the given name and display it.
	 * @param windowName: name of the window
	 */
	public MainGUI(String windowName) {
		// create main window
		this.mainFrame = new JFrame(windowName);
		this.mainFrame.setLayout(new BorderLayout());
		this.mainFrame.setAlwaysOnTop(true);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// TODO: set minimum window size after done formatting
//		this.mainMinSize = new Dimension(1000, 800);
//		this.mainFrame.setMinimumSize(mainMinSize);
		// add NORTH contents
		this.northPanel = new JPanel();
		this.northPanel.setPreferredSize(new Dimension(800, 50));
		this.northPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.mainFrame.add(this.northPanel, BorderLayout.NORTH);
		this.northPanel.add(this.newLabel("Data Directory: ", 14.0f));
		this.dataDir = this.newTextField("C:/IDE/AL_MapData/", 50, 14.0f);
		this.dataDir.setActionCommand("Set Data Directory");
		this.dataDir.addActionListener(this);
		this.northPanel.add(this.dataDir);
		// add CENTER contents
		this.centerPanel = new JPanel();
		this.centerPanel.setPreferredSize(new Dimension(500, 450));
		this.centerPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
		this.mainFrame.add(this.centerPanel, BorderLayout.CENTER);
		this.centerSubPanels = new JPanel[5];
		for (int i = 0; i < this.centerSubPanels.length; i++) {
			this.centerSubPanels[i] = new JPanel();
			this.centerSubPanels[i].setPreferredSize(new Dimension(450, 50));
			this.centerSubPanels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			this.centerPanel.add(this.centerSubPanels[i]);
		}
		// add CENTER SubPanel1 contents
		this.centerSubPanels[0].add(this.newLabel("Mouse Location: ", 16.0f));
		this.mouseCoords = this.newLabel("0, 0", 16.0f);
		this.centerSubPanels[0].add(this.mouseCoords);
		// add CENTER SubPanel2 contents
		this.centerSubPanels[1].add(this.newLabel("Color at Cursor: ", 16.0f));
		this.colorAtCursor = this.newTextField("16777215", 20, 16.0f);
		this.colorAtCursor.setEditable(false);
		this.colorAtCursor.setBackground(new Color(16777215));
		this.colorAtCursor.setForeground(new Color(this.colorAtCursor.getBackground().getRGB() ^ 0xFFFFFF)); // foreground color is the inverse of the background
		this.centerSubPanels[1].add(this.colorAtCursor);
		// add CENTER SubPanel3 contents
		this.centerSubPanels[2].add(this.newLabel("Current Map Offset: ", 16.0f));
		this.offsetX = this.newTextField("0", 5, 16.0f);
		this.offsetX.setEditable(false);
		this.offsetY = this.newTextField("0", 5, 16.0f);
		this.offsetY.setEditable(false);
		this.centerSubPanels[2].add(this.offsetX);
		this.centerSubPanels[2].add(this.offsetY);
		// add CENTER SubPanel4 contents
		this.centerSubPanels[3].add(this.newLabel("Current Fleet: ", 16.0f));
		this.fleet = this.newLabel("1", 16.0f);
		this.centerSubPanels[3].add(this.fleet);
		// add CENTER SubPanel5 contents
		this.centerSubPanels[4].add(this.newLabel("Current Task: ", 16.0f));
		this.currentTask = this.newTextField("", 20, 16.0f);
		this.currentTask.setEditable(false);
		this.centerSubPanels[4].add(this.currentTask);
		// add SOUTH contents
		this.southPanel = new JPanel();
		this.southPanel.setPreferredSize(new Dimension(800, 100));
		this.southPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.mainFrame.add(this.southPanel, BorderLayout.SOUTH);
		JButton[] southButtons = new JButton[5];
		southButtons[0] = new JButton("Up");
		southButtons[1] = new JButton("Down");
		southButtons[2] = new JButton("Left");
		southButtons[3] = new JButton("Right");
		southButtons[4] = new JButton("Reset Offset");
		for (int i = 0; i < southButtons.length; i++) {
			southButtons[i].addActionListener(this);
			this.southPanel.add(southButtons[i]);
		}
		
		this.southSubPanel1 = new JPanel();
		this.southSubPanel1.setPreferredSize(new Dimension(800, 50));
//		this.southSubPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.southPanel.add(this.southSubPanel1);
		JButton[] south1Buttons = new JButton[3];
		south1Buttons[0] = new JButton("CalibrateNoxBL");
		south1Buttons[1] = new JButton("Swap Secretary");
		south1Buttons[2] = new JButton("Toggle Live Color");
		for (int i = 0; i < south1Buttons.length; i++) {
			south1Buttons[i].addActionListener(this);
			this.southSubPanel1.add(south1Buttons[i]);
		}
		// add WEST contents
		this.westPanel = new JPanel();
		this.westPanel.setPreferredSize(new Dimension(200, 450));
		this.westPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
		this.mainFrame.add(this.westPanel, BorderLayout.WEST);
		this.westSubPanels = new JPanel[1];
		for (int i = 0; i < this.westSubPanels.length; i++) {
			this.westSubPanels[i] = new JPanel();
			this.westSubPanels[i].setPreferredSize(new Dimension(175, 50));
			this.westSubPanels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			this.westPanel.add(this.westSubPanels[i]);
		}
		// add WEST SubPanel1 contents
		this.westSubPanels[0].add(this.newLabel("Move Mouse To", 14.0f));
		this.moveMouseToX = this.newTextField("", 5, 14.0f);
		this.moveMouseToY = this.newTextField("", 5, 14.0f);
		this.moveMouseToX.setActionCommand("Move Mouse");
		this.moveMouseToY.setActionCommand("Move Mouse");
		this.moveMouseToX.addActionListener(this);
		this.moveMouseToY.addActionListener(this);
		this.westSubPanels[0].add(this.moveMouseToX);
		this.westSubPanels[0].add(this.moveMouseToY);
		this.useRelative = new JRadioButton("Use Relative");
		this.westSubPanels[0].add(this.useRelative);
		JButton Move_Mouse = new JButton("Move Mouse");
		Move_Mouse.addActionListener(this);
		this.westSubPanels[0].add(Move_Mouse);
		this.westSubPanels[0].setPreferredSize(new Dimension(175, 130));
		// add EAST contents
		this.eastPanel = new JPanel();
		this.eastPanel.setPreferredSize(new Dimension(200, 450));
		this.eastPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
		this.mainFrame.add(this.eastPanel, BorderLayout.EAST);
		this.eastSubPanels = new JPanel[1];
		for (int i = 0; i < this.eastSubPanels.length; i++) {
			this.eastSubPanels[i] = new JPanel();
			this.eastSubPanels[i].setPreferredSize(new Dimension(175, 50));
			this.eastSubPanels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			this.eastPanel.add(this.eastSubPanels[i]);
		}
		// add EAST SubPanel1 contents
		this.eastSubPanels[0].setPreferredSize(new Dimension(175, 180));
		this.eastSubPanels[0].add(this.newLabel("Map Selection", 14.0f));
		this.mapSelection = new JComboBox<String>();
		this.mapSelection.setPreferredSize(new Dimension(150, 25));
		this.mapSelection.setEditable(false);
		this.mapSelection.addItem("N/A");
		this.mapSelection.setSelectedItem("N/A");
		this.eastSubPanels[0].add(this.mapSelection);
		JButton Update_Maps = new JButton("Update Map List");
		Update_Maps.setPreferredSize(new Dimension(150, 30));
		Update_Maps.addActionListener(this);
		this.eastSubPanels[0].add(Update_Maps);
		JButton Grind_Map = new JButton("Grind Selected Map");
		Grind_Map.setPreferredSize(new Dimension(150, 30));
		Grind_Map.addActionListener(this);
		this.eastSubPanels[0].add(Grind_Map);
		JButton Stop_Grind = new JButton("Stop Grind");
		Stop_Grind.setPreferredSize(new Dimension(100, 30));
		Stop_Grind.addActionListener(this);
		this.eastSubPanels[0].add(Stop_Grind);
		// display main window
		this.mainFrame.pack();
		this.mainFrame.setLocationRelativeTo(null);
		this.mainFrame.setVisible(true);
	}
	
	/**
	 * Run loop of the GUI. Add listeners and contents as well as print debug info in the cmd line.
	 */
	public void run() {
		// add listeners
		
		// enter run loop
		boolean running = true;
		while (running) {
			this.pause(3000);
			this.mainFrame.pack();
			// DEBUG: print window dimension
//			System.out.println("Current window dimension: " + this.mainFrame.getWidth() + 
//					" x " + this.mainFrame.getHeight());
		}
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		this.task = cmd;
		// JButton SOUTH
		if (cmd.equals("Reset Offset")) {
			this.cmdResetOffset();
		} else if (cmd.equals("Read Map File")) {
			
		} else if (cmd.equals("Create Map File")) {
			
		} else if (cmd.equals("Toggle Live Color")) {
			Start.mLoc.toggleLiveColor();
		}
		// JButton WEST
		else if (cmd.equals("Move Mouse")) {
			this.cmdMoveMouse();
		}
	}
	
	/*** Commmand Handling ***/
	
	public void cmdResetOffset() {
		this.offsetX.setText("0");
		this.offsetY.setText("0");
	}
	
	public void cmdMoveMouse() {
		try {
			int x = Integer.parseInt(this.moveMouseToX.getText());
			int y = Integer.parseInt(this.moveMouseToY.getText());
			if (this.useRelative.isSelected()) {
				Start.rbtM.moveTo(new XY(x, y).add(al));
			} else {
				Start.rbtM.moveTo(new XY(x, y));
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid Coordinate Input!");
		}
		this.moveMouseToX.setText("");
		this.moveMouseToY.setText("");
	}
	
	/**
	 * Create a JTextField with the given parameters
	 * @param str: initial content of the text field
	 * @param columns: column length of the field
	 * @param fontsize: font size in float (default 12.0f)
	 * @return
	 */
	private JTextField newTextField(String str, int columns, float fontsize) {
		JTextField field = new JTextField(str, columns);
		field.setFont(field.getFont().deriveFont(fontsize));
		return field;
	}
	
	/**
	 * Helper method for creating a JLabel with the given parameters.
	 * @param str: content of the label
	 * @param width
	 * @param height
	 * @param fontsize: font size in float (default 12.0f).
	 * @return
	 */
	private JLabel newLabel(String str, int width, int height, float fontsize) {
		JLabel label = new JLabel(str, SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(width, height));
		label.setFont(label.getFont().deriveFont(fontsize));
		return label;
	}
	
	/**
	 * Create a JLabel only specifying the font size.
	 * @param str: content of the label
	 * @param fontsize: font size in float (default 12.0f).
	 * @return
	 */
	private JLabel newLabel(String str, float fontsize) {
		JLabel label = new JLabel(str);
		label.setFont(label.getFont().deriveFont(fontsize));
		return label;
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
