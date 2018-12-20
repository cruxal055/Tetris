import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


public class Board extends JPanel
{
	public static enum Movement {BOARD_UP, BOARD_DOWN, BOARD_RIGHT, BOARD_LEFT};
	public static enum CurrentState { RUNNING, PAUSED, GAME_OVER};
	
	private static int DEFAULT_ROWS = 18, DEFAULT_COLS = 10, SQUARE_SIZE = 28; //18,10, 25. 28
	private Block boardPiece[][];
	private int score, linesCleared;
	private final static int TIMER_PERIOD = 1000;
	private Timer dropRate;
	
	private boolean lockDownButton;	
	private Tetromino currPiece;
	
	private CurrentState gameState;
	
	/**
	 * default constructor
	 */
	public Board()
	{
		super();
		this.setSize(DEFAULT_COLS, DEFAULT_ROWS);		
		gameState = CurrentState.RUNNING;
		score = linesCleared = 0;
		lockDownButton = false;
		construct();
	}
	
	/**
	 * @postcondition: initializes all the member variables
	 */
	private void construct()
	{
		boardPiece = createBoard();
		dropRate = new Timer(TIMER_PERIOD,new timerListener());
	}	
	
	/**
	 * @return: returns a 2d matrix of the block object
	 */
	private Block[][] createBoard()
	{
		Block tempBoard[][] = new Block[DEFAULT_COLS][DEFAULT_ROWS];
		for(int i = 0; i < DEFAULT_COLS; ++i)
		{
			for(int j = 0; j < DEFAULT_ROWS; ++j)
			{
				tempBoard[i][j] = new Block();
			}
		}
		return tempBoard;	
	}
	
	
	private class timerListener implements ActionListener
	{
		@Override
		/**
		 * moves the tetris piece down the y axis by 1
		 * each time the timer finishes
		 */
		public void actionPerformed(ActionEvent e) 
		{
			if(gameState.equals(CurrentState.GAME_OVER))
			{
				dropRate.stop();
				return;
			}
			boolean done = moveFallingPiece(0,1); //need to check if i should keep going or not
			if(done || needResetPiece() )
			{
				newFallingPiece();
			}
			checkRows();
		}
	}

	/**
	 * @postcondition: generates a new tetris piece and places it 
	 * on the board
	 */
	private void newFallingPiece()
	{  
		gameState = CurrentState.RUNNING;
		currPiece = new Tetromino();
		currPiece.generatePiece();
		for(int i = 0; i < 3; ++i)
		{
			setUpPiece(i);
		}
		dropRate.start();
	}
	
	/**
	 * @param index: index of the boolean 2d array to set up
	 * @postcondition: sets up one row of a the tetromino piece 
	 * on the top board
	 */
	private void setUpPiece(int index)
	{
		int counter = DEFAULT_COLS/2;
		for(int i = 0; i < currPiece.lengthOfMatrix(); ++i)
		{
			if(boardPiece[counter][index].getValue())
			{
				gameState = CurrentState.GAME_OVER;
			}
			else
			{
				boardPiece[counter][index].setValue(currPiece.getBoolValue(index, i));
				boardPiece[counter][index].setColor(currPiece.getColor());
				currPiece.addPt(counter, index);
				++counter;
			}
		}
	}
	
	/**
	 * @override
	 * @postcondition: paints the background, then the board itself
	 * the block holds
	 */
	public void paintComponent(Graphics g)
	{
		super.repaint();
		this.setBackground(Color.BLACK);
		paintBoard(g);
	}
	
	/**
	 * @postcondition: paint the entire matrix depending on the color 
	 * the block holds
	 */
	public void paintBoard(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		for(int i = 0; i < DEFAULT_COLS; ++i)
		{
			for(int j = 0; j < DEFAULT_ROWS; ++j)
			{
				if(!boardPiece[i][j].getValue())
				{
					g2.setColor(Color.gray); // +200, + 100 are good sizes
				}
				else
				{
					g2.setColor(boardPiece[i][j].getColor());
				}
				g2.fillRect(i*SQUARE_SIZE, (j * SQUARE_SIZE), 
						   (SQUARE_SIZE-1), SQUARE_SIZE-1); 
			}
		}
	}
	
	/**
	 * @param dir: command given by the player
	 * @postcondition: moves the tetris piece, or rotates
	 *  depending on the command given. 
	 */
	public void move(Movement dir)
	{
		boolean needNew = false;
		if(gameState.equals(CurrentState.PAUSED) || 
		   gameState.equals(CurrentState.GAME_OVER))
		{
			return;
		}
		switch(dir)
		{
			case BOARD_DOWN:
			{
				if(!lockDownButton)
    			{
    				needNew = moveFallingPiece(0,1);
    			}
    			break;
			}
			case BOARD_RIGHT:
			{
				needNew = moveFallingPiece(1,0);
    			break;
			}
			case BOARD_LEFT:
			{
				needNew = moveFallingPiece(-1,0);
    			break;
			}
			case BOARD_UP:
			{
				clearCurrent();
    			currPiece.rotate();        			
    			setCoordinates();
    			break;
			}		
		}
		if(needNew ||  (needResetPiece()))
		{
    		newFallingPiece();
    	}
		
	}
	
	/**
	 * @param dx: change in x direction
	 * @param dy: change in y direction
	 * @return: returns whether or not the piece has encountered the
	 * end of the board once it finishes moving
	 */
	private boolean moveFallingPiece(int dx, int dy)
	{
		clearCurrent();
		boolean needNewPiece = false;
		for(int i = 0; i < currPiece.getNumOfPoints(); ++i)
		{
			if(currPiece.boolValue(i))
			{
				Point p = currPiece.getPoint(i);
				if(!isValidMove(dx,dy,p))
				{
					dx = dy = 0;
				}
				currPiece.set(i, dx, dy);
				boardPiece[p.getX()][p.getY()].setValue(true);
				boardPiece[p.getX()][p.getY()].setColor(currPiece.getColor());		
				if(p.getY() == DEFAULT_ROWS -1)
				{
					needNewPiece = true;
				}
			}
		}
		dropRate.restart();	
		return needNewPiece;
	}
	
	/**
	 * @postcondition: clears the current tetris piece off the board
	 */
	private void clearCurrent()
	{
		for(int i = 0; i < currPiece.getNumOfPoints(); ++i)
		{
			Point current = currPiece.getPoint(i);
			boardPiece[current.getX()][current.getY()].setValue(false);
		}
		repaint();
	}
	
	
	/**
	 * @param dx: change in x direction
	 * @param dy: change in y direction
	 * @param location: the original location of the block
	 * @return: returns whether or not the move should be made
	 */
	private boolean isValidMove(int dx, int dy, Point location)
	{
		int newX = location.getX() + dx, newY = location.getY() + dy;
		if(newX < 0 || newX >= DEFAULT_COLS)
		{
			return false;
		}
		if(newY == DEFAULT_ROWS)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @postcondition: removes the rows that are full of blocks, and 
	 * shifts pieces on top down
	 */
	public void checkRows()
	{
		ArrayList<Integer> allDone = new ArrayList<>();
		rowFinished(allDone);
		if(!allDone.isEmpty())
		{	
			reset(allDone);
		}
	}
	
	/**
	 * @param donePos: checks to see if there are complete
	 * rows on the board
	 */
	private void rowFinished(ArrayList<Integer> donePos)
	{
		for(int i = DEFAULT_ROWS-1; i >= 0; --i)
		{
			boolean all = true;
			for(int j = 0; j < DEFAULT_COLS; ++j)
			{
				if(!boardPiece[j][i].getValue())
				{
					all = false;
					break;
				}
			}
			if(all)
			{
				score+=10;
				donePos.add(i);
			}
			all = true;
		}
		if(!donePos.isEmpty())
		{
			linesCleared+=donePos.size();
			score*=donePos.size();
		}
	}
	
	/**
	 * @param numCleared: the number of cleared rows
	 * @postcondition: resets "numCleared" rows, aka clear then
	 * and shifts pieces on top onto their respective positions down
	 */
	public void reset(ArrayList<Integer> allCleared)
	{
		clearCurrent();
		for(int i = 0; i < allCleared.size(); ++i)
		{
			for(int j = 0; j < DEFAULT_COLS; ++j)
			{
				boardPiece[j][allCleared.get(i)].setValue(false);
			}
		}
		int i =allCleared.get(allCleared.size()-1);
		for(; i >= 1; --i)
		{
			for(int j = 0; j < DEFAULT_COLS; ++j)
			{
				boardPiece[j][i].setValue(boardPiece[j][i-1].getValue());
				boardPiece[j][i].setColor(boardPiece[j][i-1].getColor());
				boardPiece[j][i-1].setValue(false);
			}
		}
		clearCurrent();
	}
				
	
	/**
	 * @return: returns true if a the tetris piece is directly on 
	 * top of another piece, return false otherwise
	 */
	public boolean needResetPiece()
	{
		for(int i = 0; i < currPiece.getNumOfPoints(); ++i)
		{
			Point temp = new Point(currPiece.getPoint(i));
			temp.setY(temp.getY()+1);
			if(boardPiece[temp.getX()][temp.getY()].getValue())
			{
				if(!currPiece.containsPt(temp))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * @postcondition: takes the tetris piece, and its occupied spots
	 * and translates it to the board
	 */
	public void setCoordinates()
	{
		int rowToUse = 0, colToUse = 0;	
		for(int i = 0; i < currPiece.getNumOfPoints(); ++i)
		{
			Point neo = currPiece.getPoint(i);
			boardPiece[neo.getX()][neo.getY()].setValue
			(currPiece.getBoolValue(rowToUse,colToUse));
			
			boardPiece[neo.getX()][neo.getY()].setColor
			(currPiece.getColor());
			
			colToUse++;
			if(colToUse == currPiece.lengthOfMatrix())
			{
				colToUse = 0;
				rowToUse++;			
			}
		}
	}
	
	
	/**
	 * @postconditon: resumes the timer, and allows controls to be inputted again
	 */
	public void resume()
	{
		gameState = CurrentState.RUNNING;
		dropRate.start();
	}
	
	/**
	 * @postcondition: stops the timer, prevent controls from being inputted
	 */
	public void pause()
	{
		gameState = CurrentState.PAUSED;
		dropRate.stop();	
	}

	/**
	 * @return: returns the score variable
	 */
	public int getScore()
	{
		return score;
	}
	
	/**
	 * @return: returns the number of lines cleared
	 */
	public int getLinesCleared()
	{
		return linesCleared;
	}
	
	/**
	 * begins the timer variable, and allows for input
	 */
	public void beginGame()
	{
		newFallingPiece();
	}
	
	/**
	 * @return: returns an enum representing the games current state
	 */
	public CurrentState getGameState()
	{
		return gameState;
	}
	
	/**
	 * @postcondition: resets the entire board and all member variables
	 */
	public void restart()
	{
		dropRate.stop();
		score = 0;
		for(int i = 0; i < DEFAULT_COLS; ++i)
		{
			for(int j = 0; j < DEFAULT_ROWS; ++j)
			{
				boardPiece[i][j].setValue(false);
			}
		}
	}	
	
}

	
