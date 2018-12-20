
public class Point 
{
	private int x, y;
	
	/**
	 * default constructor
	 */
	public Point()
	{
		x = y = 0;
	}
	
	/**
	 * Copy constructor
	 * @param other: other object whose data values
	 * should be copied onto this
	 */
	public Point(Point other)
	{
		copyOther(other);
	}
	
	/**
	 * constructor
	 * @param x: x coordinate
	 * @param y: y coordinate
	 */
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return: returns x coordinate
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * @return: returns y coordinate
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * @postcondition: sets the x and y coordinates
	 * to what is passed in
	 * @param x: new x coordinate
	 * @param y: new y coordinate
	 */
	public void setXY(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @postcondition: sets the member x coordinate 
	 * to x passed in as a parameter
	 * @param x: new x coordinate
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 *@postcondition: sets the member y coordinate 
	 * to y passed in as a parameter
	 * @param x: new y coordinate
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 * @param other: deep copy the other point object
	 * and its data values
	 */
	public void copy(Point other)
	{
		if(other != null && this != other)
		{
			copyOther(other);
		}
	}
	
	/**
	 * @param other: the other point to copy its data values
	 * @postcondition: performs a deep copy of the other value
	 */
	private void copyOther(Point other)
	{
		this.x = other.x;
		this.y = other.y;
	}
	
	/**
	 * @override
	 * @return: returns whether or not the object contains the 
	 * same data value
	 */
	public boolean equals(Object other)
	{
		if(other != null && other instanceof Point)
		{
			Point temp = (Point) other;
			return (temp.x == x && temp.y == y);
		}
		return false;
	}

}
