import java.awt.Color;

public class Block 
{
	private boolean value;
	private Color currentColor;

	/**
	 * Constructor
	 */
	public Block()
	{
		value = false;
		currentColor = Color.black;
	}
	
	/**
	 * @return: returns the colour that the cell should be colored as
	 */
	public Color getColor()
	{
		return currentColor;
	}
	
	/**
	 * @return returns if the cell is empty or not
	 */
	public boolean getValue()
	{
		return value;
	}
	
	/**
	 * @param newValue: new boolean value that indicates the cells
	 * current condition: empty, or holding an item
	 */
	public void setValue(boolean newValue)
	{
		value = newValue;
	}
	
	/**
	 * @param newColor: sets the colour of the Block object
	 */
	public void setColor(Color newColor)
	{
		currentColor = newColor;
	}

	/**
	 * @param other: Performs a deep copy of the other object
	 * and its data values
	 */
	public void copy(Block other)
	{
		if(this != other)
		{
			currentColor = other.currentColor;
			value = other.value;
		}
	}

}
