import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class Main extends JPanel
{
	private static int TIMER_COUNTDOWN = 100;
	private Board board;
	private JLabel scoreDisp, linesDisp;
	private JButton pause, resume;
	private JPanel status, eastSide;
	private Timer checkInfo;
	
	/**
	 * Constructor
	 */
	public Main()
	{
		construct();
		configureSettings();
		this.add(board,BorderLayout.CENTER);
		setUpScore();
		wire();
		checkInfo.start();
		board.beginGame();
	
	}
	
	/**
	 * @postcondition: constructs all the swing components
	 */
	private void construct()
	{
		eastSide = new JPanel();
		board = new Board();
		scoreDisp = new JLabel("SCORE: " + 0);
		pause = new JButton("pause");
		status = new JPanel();
		resume = new JButton("resume");
		linesDisp = new JLabel("LiNES: " + 0);
		checkInfo = new Timer(TIMER_COUNTDOWN, new TimerListener());
	}
	
	/**
	 * @postcondition: Sets the settings for the components
	 */
	private void configureSettings()
	{
		this.setLayout(new BorderLayout());
		linesDisp.setForeground(Color.WHITE);
		scoreDisp.setForeground(Color.WHITE);
		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
	}
	
	/**
	 * @postcondition: sets up the score box and adds it Panel
	 */
	public void setUpScore()
	{
	   TitledBorder title = BorderFactory.createTitledBorder(null, 
               "Options", TitledBorder.CENTER, 
               TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), 
               Color.WHITE);
	   
	   Box area = Box.createVerticalBox();
	   
	   area.add(scoreDisp);
	   area.add(linesDisp);
	   area.add(pause);
	   area.setBorder(title);
	   area.setOpaque(true);
	   area.setBackground(Color.gray);
	   
	   resume.addActionListener(new ActionListen());
	   area.add(resume);
	   eastSide.setLayout(new GridLayout(1,3));
	   eastSide.add(area);
	   this.setBackground(Color.BLACK);
	   this.add(eastSide,BorderLayout.EAST);
	}
	

 	
 	/**
 	 * @postcondition: wires the components to their listeners
 	 */
	private void wire()
	{
		this.addKeyListener(new keyListen());
		ActionListen action = new ActionListen();
		resume.addActionListener(action);
		pause.addActionListener(action);
	}
	
	
	private class ActionListen implements ActionListener
	{
		@Override
		/**
		 * @postcondition: Either sets the state of board to be paused, or to resume 
		 * depending on the button pressed
		 */
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getActionCommand().equals(pause.getActionCommand()))
			{
				board.pause();
			}
			else
			{
				if(e.getActionCommand().equals(resume.getActionCommand()))
				{
					board.resume();
				}
			}
			resumeFocus();
		}
	}
	
	/**
	 * @postcondition: shifts the focus back to the main gui
	 */
 	private void resumeFocus()
 	{
 		this.setFocusable(true);
 		this.requestFocus();
 	}
	
	private class TimerListener implements ActionListener
	{
		@Override
		/**
		 * @postcondition: checks the games state and updates
		 * infortmation
		 */
		public void actionPerformed(ActionEvent e) 
		{
			if(board.getGameState().equals(Board.CurrentState.GAME_OVER))
			{
				gameOver();
			}
			else
			{
				scoreDisp.setText("SCORE: " + board.getScore());
				linesDisp.setText("LINES: " + board.getLinesCleared());
				checkInfo.restart();
			}
		}
	}
	
	/**
	 * @postcondition: Displays a message stating that the game is over,
	 * and restarts the board once the user acknowlecges it
	 */
	private void gameOver()
	{
		String message= "Game over, your score was: " + board.getScore();
		int n = JOptionPane.showConfirmDialog(null,
                message,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE);
		if(n == JOptionPane.OK_OPTION)
		{
			board.restart();
			scoreDisp.setText("SCORE: " + 0);
			linesDisp.setText("LINES CLEARED: " + 0);
			board.beginGame();
		}
	}
 

	private class keyListen extends KeyAdapter
	{
		/**
		 * @postcondition: Listens to keyboard presses, and directs
		 * the infortmation to the board
		 */
	  public void keyPressed(KeyEvent e) 
	  {

	  	switch(e.getKeyCode())
	  	{
	  		case KeyEvent.VK_DOWN:
	  		{
	  			board.move(Board.Movement.BOARD_DOWN);
	  			break;
	  		}
	  		case KeyEvent.VK_RIGHT:
	  		{
	  			board.move(Board.Movement.BOARD_RIGHT);
	  			break;
	  		}
	  		case KeyEvent.VK_LEFT:
	  		{
	  			board.move(Board.Movement.BOARD_LEFT);
	  			break;
	  		}
	  		
	  		case KeyEvent.VK_UP:
	  		{
	  			board.move(Board.Movement.BOARD_UP); 
	  			break;
	  		}
	  	}
	  }
	 
	}

	public static void main(String args[])
	{
		
		JFrame frame = new JFrame("Tetris");
		frame.add(new Main());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.setSize(382,525);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
