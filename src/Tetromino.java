import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JPanel;

import java.util.ArrayList;

/**
 * @author Alan Lu
 * @references:
 * http://www.java67.com/2016/10/3-ways-to-reverse-array-in-java-coding-interview-question.html
 */

public class Tetromino 
{
	private static final boolean[][] I_PIECE = {
	  { true, true, true, true},
	  {false,false,false,false },
	  {false, false, false, false}
	  };
		  
	  private static final boolean[][] J_PIECE = {
	    { true, false, false },
	    { true, true,  true },
		{false, false, false }

	  };
	  
	  private static final boolean[][] L_PIECE = {
	    { false, false, true},
	    { true,  true,  true},
		{false, false, false}

	  };
	  
	  private static final boolean[][] O_PIECE = {
	    { true, true},
	    { true, true},
	    {false, false}
	  };
	  
	  private static final boolean[][] S_PIECE = {
	    { false, true, true},
	    { true,  true, false },
	    {false, false, false}
	  };
	  
	  private static final boolean[][] T_PIECE = {
	    { false, true, false },
	    { true,  true, true},
	    { false, false, false}
	  };
	
	  private static final boolean[][] Z_PIECE = {
	    { true,  true, false },
	    { false, true, true},
	    {false, false, false}
	  };
	  
	  private static final Color[] PIECE_COLORS = 
	{
		 Color.red, Color.yellow, Color.magenta, Color.pink,
		 Color.cyan, Color.green, Color.orange		
	};
	
	private static final boolean PIECES[][][] =
	{
			I_PIECE, J_PIECE, L_PIECE, O_PIECE, S_PIECE, T_PIECE, Z_PIECE		
	};

	
	private boolean currentPiece[][];
	private Color colorValue;
	private ArrayList<Point> occupiedPts;
	private Point center;
	private boolean isIPiece, isOPiece;
	
	/**
	 * constructor
	 */
	public Tetromino()
	{
		occupiedPts = new ArrayList<Point>();
		isIPiece = isOPiece = false;
	}
	
	/**
	 * @postcondition: generates a random 2d array and assigns it
	 * to currentPiece
	 */
	public void generatePiece()
	{
		isIPiece = isOPiece = false;
		int rando = (int)(Math.random() * 7 );
		if(rando == 0)
		{
			isIPiece = true;
		}
		if(rando == 3)
		{
			isOPiece = true;
		}
		currentPiece = PIECES[rando];	
		colorValue = PIECE_COLORS[rando];
	}

	/**
	 * Rotation functions
	 */
	
	/**
	 * @postcondition: rotates the current tetris piece, shifting 
	 * values depending on its current rotation
	 */
	public void rotate()
	{
		if(currentPiece.equals(O_PIECE))
		{
			return;
		}
		else
		{
			if(isIPiece)
			{
				currentPiece = rotateStraight(currentPiece);
				resetStraightPoints();
			}
			else
			{
				currentPiece = reverse(currentPiece);	
				resetPoints();
			}
		}
	}
	
	/**
	 * @param arr: 2d array with 4 consecutive trues 
	 * that needs to be rotated
	 * @return: returns the rotated version of arr
	 */
	public boolean[][] rotateStraight(boolean arr[][])
	{
		boolean neo[][];
		if(arr.length == 3)
		{
			neo = new boolean[4][3];
			int counter = 0;
			for(int i = 2; i >= 0; --i)
			{
				for(int j = 0; j <= 3; ++j)
				{
					neo[j][counter] = arr[i][j];
				}
				++counter;
			}
		}
		else
			{
				neo = new boolean[3][4];
				for(int i = 0; i < 3; ++ i)
				{
					int counter = 0;
					for(int j = 3; j >= 0; --j)
					{
						neo[i][counter] = arr[j][i];
						++counter;
					}
					counter = 0;
				}
			}
			return neo;	
		}
		
		/**
		 * @postcondition: changes all the points the object current occupies to
		 * meet the 2D array rotation
		 */
		public void resetStraightPoints()
		{
			ArrayList<Point> newVer = new ArrayList<>();
			int counter = 0;
			if(currentPiece.length == 4)
		{
			for(int i = 0; i < currentPiece.length-1; ++i)
			{
				for(int j = 0; j < 3; ++j)
				{
					newVer.add(occupiedPts.get(counter));
					++counter;
				}
				counter++;
			}
			int initialSize = newVer.size();
			for(int i = 3; i >= 1; --i)
			{
				Point temp = new Point(newVer.get(initialSize-i));
				temp.setY(temp.getY()+1);
				newVer.add(temp);
			}
		}
		else
		{
			for(int i = 0; i < 3; ++i)
			{
				for(int j = 0; j < 3; ++j)
				{
					newVer.add(occupiedPts.get(counter));
					++counter;
				}
				Point lastItem = new Point(newVer.get(newVer.size()-1));
				lastItem.setX(lastItem.getX()+1);
				newVer.add(lastItem);
			}
		}
		occupiedPts = newVer;
	}
	
	  /**
	   * @param arr: 2d array that should be reversed, this should be
	   * used when the array has 3 rows and 2 columns
	   * @return: returns the array rotated 
	   */
	public boolean[][] reverse(boolean arr[][])
	{
		boolean neo[][] = new boolean[3][3];
		for(int i = 0; i <= 2; ++i)
		{
			int counter1 = 2;
			for(int j = 0; j < 3; ++j)
			{
				neo[i][j] = arr[counter1][i];
				--counter1;
			}
		}
		center = occupiedPts.get(4);
		return neo;			
	}
	
	/**
	 * @postcondition: sets all points to their respective new points
	 * based on the center coordinate
	 */
	private void resetPoints()
	{	
		occupiedPts.clear();
		for(int i = 0; i < currentPiece.length; ++i)
		{
			for(int j = 0; j < currentPiece[i].length; ++j)
			{
				Point temp = new Point(center.getX(),center.getY());
				if(i < 1)
				{
					temp.setY(center.getY() - 1);
				}
				else
				{
					if(i > 1)
						temp.setY(center.getY() + 1);
				}
				if(j > 1)
				{
					temp.setX(center.getX() + 1);
				}
				else
				{
					if(j < 1)
						temp.setX(center.getX() - 1);
				}
				
				if(currentPiece.length == 3)
				{
					if(i == 1 && j == 1)
					{
						occupiedPts.add(center);
					}
					else
					{
						occupiedPts.add(temp);
					}
				}
			}
		}
	}
	
	/**
	 * Mutators
	 */
	
	/**
	 * 
	 * @param x: x coordinate of the new point
	 * @param y: y coordinate of the new point
	 */
	public void addPt(int x, int y)
	{
		occupiedPts.add(new Point(x,y));
	}
	
	/**
	 * @param dx: change in x coordinates
	 * @param dy: change in y coordinates
	 * @postcondition: updates all of the points based
	 * on the changes in x and y.
	 */
	public void changePts(int dx, int dy)
	{
		Point ptr;
		for(int i = 0; i < occupiedPts.size(); ++i)
		{
			ptr = occupiedPts.get(i);
			ptr.setXY(ptr.getX()+dx, ptr.getY()+dy);
			occupiedPts.set(i, ptr);
		}
	}	
		
	/**
	 * @param index: index from the ArrayList of points, 
	 * indicates which point to change from the arraylist
	 * @param dx: change in x
	 * @param dy: change in y
	 */
	public void set(int index, int dx, int dy)
	{
		Point temp = occupiedPts.get(index);
		temp.setXY(temp.getX() + dx, temp.getY() + dy);
		occupiedPts.set(index, temp);
	}
	
	/**
	 * Accessors
	 */

	/**
	 * @param row: row of the matrix to access
	 * @param col: column of the matrix to access
	 * @return: returns the boolean value located in 
	 * the row and column provided
	 */
	public boolean getBoolValue(int row, int col)
	{
		return currentPiece[row][col];	
	}
	
	/**
	 * @param p: point to check
	 * @return: returns whether p exists in the occupiedPts arrayList
	 */
	public boolean containsPt(Point p)
	{
		return occupiedPts.contains(p);
	}
	
	/**
	 * @return: number of points currently 
	 * occupied by the tetris piece
	 */
	public int getNumOfPoints()
	{
		return occupiedPts.size();
	}
	
	/**
	 * @param index: index to access
	 * @return: returns the point at the index
	 */
	public Point getPoint(int index)
	{
		return occupiedPts.get(index);
	}
	
	/**
	 * @return: returns ColorValue;
	 */
	public Color getColor()
	{
		return colorValue;
	}
	
	/*
	 * @return: returns the length of the matrix
	 * the object currently holds
	 */
	public int lengthOfEntireMatrix()
	{
		return currentPiece.length;
	}
	
	/**
	 * @returns the length of the 1 row of the
	 * current matrix
	 */
	public int lengthOfMatrix()
	{
		return currentPiece[0].length;
	}
	
	/**
	 * @return: returns true if the current 2D array 
	 * represents a O tetris piece, false otherwise
	 */
	public boolean getIsOPiece()
	{
		return isOPiece;
	}
	
	/** 
	 * @param pos: index to get the bool value for, also corresponds
	 * to its placement on the ArrayList
	 * @return: returns the bool value at "pos" in the 
	 * 2D array currentPiece
	 */
	public boolean boolValue(int pos)
	{
		if(isIPiece)
		{
			if(currentPiece.length == 3)
			{
				return currentPiece[pos/4][pos%4];
			}
			else
			{
				return currentPiece[pos/3][pos%3];
			}
			
		}
		if(isOPiece)
		{
			return currentPiece[pos/2][pos%currentPiece[0].length];
		}
		return currentPiece[pos/currentPiece.length][pos%currentPiece[0].length];
	}
	
	
	
}

