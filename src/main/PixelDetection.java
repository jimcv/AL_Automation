package main;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Robot that provides color detection and conversion
 * @author Jimmy Sheng
 */
public class PixelDetection {
	private Robot rbt;			// robot object
	private BufferedImage img;	// img to analyze
	
	private Rectangle rect;		// screen capture rectangle
	private Point p;			// upper left coords of  rectangle
	private Dimension d;		// dimension of rectangle
	private int width;			// width of rectangle in int
	private int height;			// height of rectangle in int
	
	private Color pColor;
	
	public static final XY SCAN_B = new XY(30, 30);					// big scan size
	public static final XY SCAN_S = new XY(16, 16);					// small scan size
	
	/**
	 * Constructor that initializes the robot
	 */
	public PixelDetection() {
		try {
			this.rbt = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the scanning rectangle.
	 * @param rect: input rectangle
	 */
	public void setRect(Rectangle rect) {
		this.rect = rect;
		this.p = rect.getLocation();
		this.d = rect.getSize();
		this.width = (int) rect.getWidth();
		this.height = (int) rect.getHeight();
	}
	
	/**
	 * Set the scanning rectangle.
	 * @param pt: upper-left corner of the rectangle
	 * @param dim: dimension of the rectangle
	 */
	public void setRect(XY pt, XY dim) {
		this.width = dim.getX();
		this.height = dim.getY();
		this.p = new Point(pt.getX(), pt.getY());
		this.d = new Dimension(this.width, this.height);
		this.rect = new Rectangle(this.p, this.d);
	}
	
	/**
	 * Return true if at the given upper left corner, the given RGB color is present in the big scan rectangle.
	 * @param point
	 * @param color
	 * @return
	 */
	public boolean scanB(XY point, int color) {
		this.setRect(point, SCAN_B);
		return this.containsRGB(color);
	}
	
	/**
	 * Return true if at the given center, the given RGB color is present in the small scan rectangle.
	 * @param point
	 * @param color
	 * @return
	 */
	public boolean scanS(XY point, int color) {
		this.setRect(point.minus(SCAN_S.div(2)), SCAN_S);
		return this.containsRGB(color);
	}
	
	/**
	 * Return if the current rectangle contains the given integer AlphaRGB color.
	 * @param c
	 * @return
	 */
	public boolean contains(int c) {
		boolean b = false;
		this.img = this.rbt.createScreenCapture(this.rect);
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; i++) {
				if (this.img.getRGB(i, j) == c) {
					b = true;
					break;
				}
			}
			if (b) break;
		}
		return b;
	}
	
	/**
	 * Return if the current rectangle contains the given integer color, ignoring Alpha.
	 * @param c
	 * @return
	 */
	public boolean containsRGB(int c) {
		boolean b = false;
		c = c & 0xFFFFFF;
		this.img = this.rbt.createScreenCapture(this.rect);
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				// TODO: implement tolerance instead of ==
				if ((this.img.getRGB(i, j) & 0xFFFFFF) == c) {
					b = true;
					break;
				}
			}
			if (b) break;
		}
		return b;
	}
	
	/**
	 * Return the color at the given pixel.
	 * @param pt
	 * @return
	 */
	public Color getColor(XY pt) {
		this.pColor = rbt.getPixelColor(pt.getX(), pt.getY());
		return this.pColor;
	}
	
	/**
	 * Return the integer AlphaRGB color at the given pixel.
	 * @param pt
	 * @return
	 */
	public int getAlphaRGBColor(XY pt) {
		return this.getColor(pt).getRGB();
	}
	
	/**
	 * Return the integer RGB color at the given pixel.
	 * @param pt
	 * @return
	 */
	public int getRGBColor(XY pt) {
		return this.getAlphaRGBColor(pt) & 0xFFFFFF;
	}
	
	/**
	 * Return the inverse integer AlphaRGB color at the given pixel.
	 * @param pt
	 * @return
	 */
	public int getInverseAlphaRGBColor(XY pt) {
		return this.getAlphaRGBColor(pt) ^ 0xFFFFFF;
	}
	
	/**
	 * Return the inverse integer RGB color at the given pixel.
	 * @param pt
	 * @return
	 */
	public int getInverseRGBColor(XY pt) {
		return this.getRGBColor(pt) ^ 0xFFFFFF;
	}
	
	/**
	 * Return the RGB color at the given pixel in hex format.
	 * @param pt
	 * @return
	 */
	public String getHexColor(XY pt) {
		return this.intToHex(getRGBColor(pt));
	}
	
	/**
	 * Convert the given integer color to hex format, filtering out Alpha.
	 * @param c
	 * @return
	 */
	public String intToHex(int c) {
		return "#" + String.format("%06X", new Integer(c & 0xFFFFFF));
	}
	
}
