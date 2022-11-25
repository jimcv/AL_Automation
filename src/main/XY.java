package main;

public class XY {
	
	// FIELDS
	private int X;
	private int Y;
	
	public static final XY O = new XY(0, 0);	// origin constant
	
	// CONSTRUCTOR
	public XY(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	public XY(double x, double y) {
		this.X = (int) x;
		this.Y = (int) y;
	}
	
	// GET/SET
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
		
	public void set(int x, int y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void set(double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void setX(int x) {
		this.X = x;
	}
	
	public void setX(double x) {
		this.X = (int) x;
	}
	
	public void setY(int y) {
		this.Y = y;
	}
	
	public void setY(double y) {
		this.Y = (int) y;
	}
	
	// PUBLIC METHODS
	/**
	 * Performs 2D vector addition
	 * @param u	- vector to add
	 * @return result	- resulting vector XY
	 */
	public XY add(XY u) {
		XY result = new XY(this.getX() + u.getX(), this.getY() + u.getY());
		return result;
	}
	
	public XY add(int x, int y) {
		XY u = new XY(x, y);
		return this.add(u);
	}
	
	public XY addX(int x) {
		XY result = this.add(x, 0);
		return result;
	}
	
	public XY addX(XY u) {
		return this.addX(u.getX());
	}
	
	public XY addY(int y) {
		XY result = this.add(0, y);
		return result;
	}
	
	public XY addY(XY u) {
		return this.addY(u.getY());
	}
	
	public XY minus(XY u) {
		XY result = new XY(this.getX() - u.getX(), this.getY() - u.getY());
		return result;
	}
	
	public XY minus(int x, int y) {
		XY u = new XY(x, y);
		return this.minus(u);
	}
	
	public XY minusX(int x) {
		XY result = this.minus(new XY(x, 0));
		return result;
	}
	
	public XY minusX(XY u) {
		XY result = this.minus(new XY(u.getX(), 0));
		return result;
	}
	
	public XY minusY(int y) {
		XY result = this.minus(new XY(0, y));
		return result;
	}
	
	public XY minusY(XY u) {
		XY result = this.minus(new XY(0, u.getY()));
		return result;
	}
	
	/**
	 * Multiply this by the given constant.
	 * @param k
	 * @return
	 */
	public XY multi(int k) {
		return new XY(this.X * k, this.Y * k);
	}
	
	/**
	 * Perform integer division on this by the given constant.
	 * @param k
	 * @return
	 */
	public XY div(int k) {
		return new XY(this.X / k, this.Y / k);
	}
	
	/**
	 * Perform a deep copy where this will take the same x and y values than u.
	 * @param u
	 */
	public void cpy(XY u) {
		this.set(u.getX(), u.getY());
	}
	
	public String toString() {
		return this.getX() + ", " + this.getY();
	}
	
	/**
	 * hashCode function copied from java Dimension.
	 */
    public int hashCode() {
        int sum = this.X + this.Y;
        return sum * (sum + 1)/2 + this.X;
    }
    
    /**
     * equals function copied from java Dimension.
     */
    public boolean equals(Object obj) {
        if (obj instanceof XY) {
            XY pos = (XY)obj;
            return (this.X == pos.X) && (this.Y == pos.Y);
        }
        return false;
    }

	// STATIC
	public static double getDx(XY pos0, XY pos1) {
		return pos1.getX() - pos0.getX();
	}
	
	public static double getDy(XY pos0, XY pos1) {
		return pos1.getY() - pos0.getY();
	}
	
	public static double getDistance(XY pos0, XY pos1) {
		double dx = getDx(pos0, pos1);
		double dy = getDy(pos0, pos1);
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	// PRIVATE METHODS
	
}
