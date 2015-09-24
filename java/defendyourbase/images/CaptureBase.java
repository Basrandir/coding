import java.util.ArrayList;
import javax.swing.*;		// Import statements for basic graphics implementation
import java.awt.event.*;
import java.awt.*;

public class CaptureBase extends JFrame implements MouseListener, KeyListener
{
    // Declare Objects and attributes
	private static Graphics2D graphics; 
    private static PaintSurface canvas; // canvas in which the game is drawn
    private static End finish;  // object that handles the end of game
    private static Start begin; // displays title screen
    static String operatingSystem; // the operating system of the computer

    // size of the screens
    protected static final int JWIDTH = 1024; 
    protected static final int JHEIGHT = 768;

    private static int DEFAULT_FPS = 30; // slows the game down, to preserve resources
    private volatile boolean running = true; // is the program runing?
    private volatile boolean starting = true; // tells the program whether it is at the intro screen or not
    private volatile boolean finishing = false; // tells the program whether it is at the end screen or not

    private volatile String winner; // displays who is the winner
    private String playerOneName = ""; // name of player one
    private String playerTwoName = ""; // name of player two
	
    private KeyMonitor keyMonitor; //  monitors the keyboard
    private Thread keyBinding; // binds certain keys to certain actions

	public static void main (String[] args)
	{
	    operatingSystem = System.getProperty("os.name"); // gets OS
	    new CaptureBase(1000 / DEFAULT_FPS * 1000000); // runs program (FPS are converted to nanoseconds)
	}

    public CaptureBase(long period)
    {
    	this.setSize(JWIDTH, JHEIGHT); // sets size of window
    	this.setTitle("Capture Base"); // sets title of window
		this.setResizable(true); // allows it to be resized, even though the map is not relative to window size
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		begin = new Start(); // creates intro screen
		finish = new End(); // creates exit screen, for some reason

		// sets up the intro screen to display
		begin.setFocusable(true);
		begin.addMouseListener(this);
		begin.addKeyListener(this);
		this.add(begin);
		this.setVisible(true);

		while(starting)
		{
			// paints the intro on the screen until the user clicks start
			begin.repaint();
		}

		// gets rid of intro window
		this.remove(begin);

		// starts the actual game
		canvas = new PaintSurface();
		canvas.setFocusable(true); // sets the canvas focusable

		// begins monitoring the keyboard for input
		keyMonitor = new KeyMonitor();
		keyBinding = new Thread(keyMonitor);
		keyBinding.start();

		this.add(canvas);
		this.setVisible(true);	// Allows user to see the game

		long beforeTime, afterTime, timeDiff, sleepTime;
		int noDelays = 0;

		beforeTime = System.currentTimeMillis(); // calculates the current time

		while(running)
		{
			// game loop
			canvas.repaint(); // continues to repaint the canvas
			afterTime = System.currentTimeMillis(); // calculates current time
			timeDiff = afterTime - beforeTime;	// subtracts two times to determine how long it took for one 'canvas.repaint()'
			sleepTime = period - timeDiff; // subtracts time taken by fps (in nanoseconds)
			if (sleepTime > 0)
				// if it's going really fast. We slow it down by calling Thread.sleep (should be approximately 30 FPS)
			{
				try
				{
					Thread.sleep(sleepTime/1000000);
				}
				catch (InterruptedException e){}
			}
			else
				Thread.yield(); // If it's going to slow, we allow any thread that can yield to yield, this helps speed up the program
	    
			beforeTime = System.currentTimeMillis();
		}
	
		// game is over by this point, so we don't need that anymore
		this.remove(canvas);

		// sets up finish screen
		finish.setFocusable(true);
		finish.addMouseListener(this);
	
		this.add(finish);
		this.setVisible(true);
	
		// I think this makes it run forever, until closed by user mouse clicks
		finishing = true;
		while(finishing){}
		this.remove(finish);
    }
	
	public void mouseClicked(MouseEvent e)
	{
		int xPos = e.getX();
		int yPos = e.getY();
		
		if(starting)
		{
		    // we decided not to use standard text fields for some reason, ask Bassam why
		    // so we have hacked up ones. This monitors which text field has focus to allow typing, based on mouse click
			
			// The reason I did this is because swing text fields are ugly. - Bassam
			begin.playerOneCursor = false;
			begin.playerTwoCursor = false;
			if (55 <= xPos && 235 >= xPos && 294 <= yPos && 320 >= yPos)
				// if player one's text field has focus
				begin.playerOneCursor = true;
			else if (55 <= yPos && 235 >= xPos && 380 <= yPos && 405 >= yPos)
				// if player two's text field has focus
				begin.playerTwoCursor = true;
				
			// begins game, and sets usernames to player one and player two if none were inputted
			if (775 <= xPos && 830 >= xPos && 253 <= yPos && 267 >= yPos)
			{
				if(playerOneName.isEmpty())
					playerOneName = "player one";
				if(playerTwoName.isEmpty())
					playerTwoName = "player two";
				starting = false;
			}
			if (775 <= xPos && 900 >= xPos && 275 <= yPos && 293 >= yPos) // toggles on and off the help screen when 'how to play' is clicked
				begin.help = !begin.help;
		}

		// we made a fake button here, for some reason, again
		// Because real buttons are ugly
		if(finishing)
		{
			if (50 <= xPos && 130 >= xPos && 153 <= yPos && 178 >= yPos)
			    System.exit(0); // when fake(awesome)button is clicked, exit.
		}
	}

    // neglected methods and neglected
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

    public void keyPressed (KeyEvent e)
    {
	// key binding is used for user movement, this is just for entering the names at the beginning
	// we use this because key listeners monitor for every key, which is what we need
		char keyChar = e.getKeyChar();
		int keyCode = e.getKeyCode();
		
		// tells the program which variable to set, based on focus given with mouse
		if (begin.playerOneCursor)
			// starts adding name if the player one text field is in focus
		{
			if (playerOneName.length() > 0 && keyCode == 8)
				playerOneName = playerOneName.substring(0, playerOneName.length() - 1);
			else if (keyCode != 8 && begin.fm.stringWidth(playerOneName) <= 175)
				playerOneName += keyChar;
		}
		else if (begin.playerTwoCursor)
			// starts adding name if the player two text field is in focus
		{
			if (playerTwoName.length() > 0 && keyCode == 8)
				playerTwoName = playerTwoName.substring(0, playerTwoName.length() - 1);
			else if (keyCode != 8 && begin.fm.stringWidth(playerTwoName) <= 175)
				playerTwoName += keyChar;
		}
    }
	public void keyReleased (KeyEvent e){}
	public void keyTyped (KeyEvent e){}

	/*THE BEAST OF THE PROGRAM!*/
	private class PaintSurface extends JComponent
	{
		LoadTile tileLoader = new LoadTile(); // Loads the map (map1.txt) and the images associated with the map - ramp image is never used but stays just incase of future development
		Tile[][] tiles = tileLoader.getTiles(); // an array of tiles - 64 by 48 (or 1024 / 16 by 768 / 16)

		Player playerOne = new Player(playerOneName, tiles[26][17], tiles); // creates player one whose position is defined by the second parameter
		Player playerTwo = new Player(playerTwoName, tiles[36][17], tiles); // creates player two whose position is defined by the second parameter
		
		// The reason for the use of Array Lists instead of array is because they are dynamic
		ArrayList<Fireball> fireballs = new ArrayList<Fireball>(0); // Creates an array list of fireballs with initial size 0
		ArrayList<Mine> mines = new ArrayList<Mine>(0); // creates an array list of mines with initia size 0
		
		public void paint (Graphics g)
		{
			graphics = (Graphics2D)g; // casts to graphics2D, has better functionality
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // antialiasing to make graphics look nicer

			playerOne.move(playerOne.getDirection()); // moves player one based on direction (direction determined by which key is pressed)
			playerTwo.move(playerTwo.getDirection()); // above

			for(int y = 0 ; y < tileLoader.getAmount() ; y++) // the amount of tiles on the y-axis
			{
				for(int x = 0 ; x < tileLoader.getLine().length() ; x++) // the amount of tiles on the x-axis
					graphics.drawImage(tiles[x][y].img, x * 16, y * 16, this); // draws each tile (using the image associated with it (determined when tileLoader and tile are first initialized)
			}
			
			graphics.setColor(Color.CYAN); // ugly colour
			graphics.fillOval(playerOne.getXPos(), playerOne.getYPos(), playerTwo.getSize(), playerOne.getSize());  // Draws player one
			graphics.drawString("Money: " + playerOne.getMoney(), playerOne.getXPos() - 25, playerOne.getYPos() + 25);  // Draw money count for player one
			graphics.setColor(Color.GREEN);
			graphics.fillOval(playerTwo.getXPos(), playerTwo.getYPos(), playerTwo.getSize(), playerTwo.getSize());  // Draws player two
			graphics.drawString("Money: " + playerTwo.getMoney(), playerTwo.getXPos() - 25, playerTwo.getYPos() + 25);  // Draw money count for player two

			/*FIREBALLS*/
			graphics.setColor(Color.RED); // fireballs are red
			for (int i = 0 ; i < fireballs.size() ; i++) // number of fireballs (originally 0)
			{
				graphics.fillOval(fireballs.get(i).getXPos(), fireballs.get(i).getYPos(), 16, 16); // draws fireball
				fireballs.get(i).move(); // moves fireball
				if(fireballs.get(i).getEnd())
					fireballs.get(i).flipDirection(); // flips the direction of the fireball if it hits an occupied tile (that's what end is for)
				
				if(fireballs.get(i).getXPos() == playerTwo.getXPos() && fireballs.get(i).getYPos() == playerTwo.getYPos())
					// if statement occurs if a fireball and player two share the same position
				{
					fireballs.remove(i); // removes fireball
					playerTwo.setTile(tiles[36][17]); // sets player two to initial location (near the center)
				}
			} // repeat for each fireball

			graphics.setColor(Color.BLUE); // mines are blue
			for (int i = 0 ; i < mines.size() ; i++) // number of mines (originally 0)
			{
				if (mines.get(i).getCounter() < 100) 	// mines are drawn as long as their counter is below 100
														// once their counter reaches 100 we stop drawing them but
														// they are still there. just not visible (underground if you will)
				{
					graphics.fillOval(mines.get(i).getXPos(), mines.get(i).getYPos(), 16, 16); // draws mine
					mines.get(i).incrementCounter(); // increases counter
				}
				
				if(mines.get(i).getXPos() == playerOne.getXPos() && mines.get(i).getYPos() == playerOne.getYPos())
					// if statement occurs if a mine and player one share the same position
				{
					mines.remove(i); // removes mine
					playerOne.setTile(tiles[26][17]); // sets player one to initial location (near the center)
				}
			} // repeat for each mine
			
			/*Determining Winner*/
			if ((tiles[playerOne.getXPos() / 16][playerOne.getYPos() / 16].getType()) == '2')
				// if statement occurs if player one is on player two's base
			{
				// creates a timer above player one that determines how much longer until player one has captured the base
				graphics.setColor(Color.CYAN); // ugly colour
				graphics.drawRect(playerOne.getXPos() + 8, playerOne.getYPos() - 16, 64, 8); // draws outer progress bar
				graphics.fillRect(playerOne.getXPos() + 8, playerOne.getYPos() - 16, playerOne.getProgress(), 8); // draws increasing progress bar determined by the value gotten from 'playerOne.getProgress()'
				graphics.drawString("Timer: ", playerOne.getXPos() - 32, playerOne.getYPos() - 8); // Simply writes out, "Timer"
				
				if (playerOne.getIncrease())
					playerOne.increaseProgress(1); // increases progress by one
					
				playerOne.setIncrease(!playerOne.getIncrease()); //switches increase. This whole increase true and false thing is done to just slow down the speed of the progress bar by half
				
				if (playerOne.getProgress() == 65) // when the progress bar is complete
				{
					running = false; // game ends
					winner = playerOne.getName(); // player one is the winner
				}
			}
			else
				playerOne.setProgress(0); // progress is automatically set to 0 if the player is not on the base or moves off it

			if ((tiles[playerTwo.getXPos() / 16][playerTwo.getYPos() / 16].getType()) == '1')
				// essentially the exact same thing is done with player two to determine if he/she is capturing player one's base
			{
				graphics.setColor(Color.GREEN);
				graphics.drawRect(playerTwo.getXPos() + 8, playerTwo.getYPos() - 16, 64, 8);
				graphics.fillRect(playerTwo.getXPos() + 8, playerTwo.getYPos() - 16, playerTwo.getProgress(), 8);
				graphics.drawString("Timer: ", playerTwo.getXPos() - 32, playerTwo.getYPos() - 8);

				if (playerTwo.getIncrease())
					playerTwo.increaseProgress(1);

				playerTwo.setIncrease(!playerTwo.getIncrease());

				if (playerTwo.getProgress() == 65)
				{
					running = false;
					winner = playerTwo.getName();
				}
			}
			else
				playerTwo.setProgress(0);
				
			/*MONEY*/
			// For the next two if statements, we determine if the tile a player is on contains a mineral (coin)
			// if it does, then the player gains 50 (dollars?) and the coin is removed (tile changes to grass)
			if ((tiles[playerOne.getXPos() / 16][playerOne.getYPos() / 16].getType()) == 'M')
			{
				playerOne.changeMoney(50);
				tiles[playerOne.getXPos() / 16][playerOne.getYPos() / 16].setTile('0');
			}
			
			if ((tiles[playerTwo.getXPos() / 16][playerTwo.getYPos() / 16].getType()) == 'M')
			{
				playerTwo.changeMoney(50);
				tiles[playerTwo.getXPos() / 16][playerTwo.getYPos() / 16].setTile('0');
			}
		}
	}

	private class End extends JPanel
	{
		private Image endScreen; // the ending darth vader image
		private Font gameWinner = new Font("Arial Black", Font.BOLD, 48); // Creates a font that displays who won

		public End()
			// image is set depending on which operating system the program is being run on
		{
			if (operatingSystem.contains("Windows"))
				endScreen = (new ImageIcon("images\\darthVaderEnd.jpg")).getImage();
			else
				endScreen = (new ImageIcon("images/darthVaderEnd.jpg")).getImage();
		}

		public void paint (Graphics g)
		{
			graphics = (Graphics2D)g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			graphics.setFont(gameWinner);
			graphics.drawImage(endScreen, 0, 0, this); // draws the background image
			graphics.drawString(winner + " wins!", 50, 125); // draws who won
		}
	}
	
	private class Start extends JPanel
	{
		private boolean help = false;				// if help is click or not
		private boolean playerOneCursor = false;	// if we're on the player one text field
		private boolean playerTwoCursor = false;	// if we're on the player two text field

		FontMetrics fm;	// font metrics help us determine an approximate size of the string in the text field (helps us not go passed the text field when writting names)
		private Image startScreen;
		private Font helpTitle = new Font("Arial Black", Font.BOLD, 20);
		private Font helpInfo = new Font("Arial", Font.BOLD, 15);
			
		public Start()
		{
			if (operatingSystem.contains("Windows"))
				startScreen = (new ImageIcon("images\\darthVader.jpg")).getImage();
			else
				startScreen = (new ImageIcon("images/darthVader.jpg")).getImage();
		}

		public void paint (Graphics g)
		{
			graphics = (Graphics2D)g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			fm = graphics.getFontMetrics(); // we get the font metrics depending on the current graphcis component used

			graphics.drawImage(startScreen, 0, 0, this);
			graphics.setColor(Color.BLACK);
			graphics.drawString(playerOneName, 55, 315); // draws the players names as they are written
			graphics.drawString(playerTwoName, 55, 400);
			
			if(help)
			{
				graphics.setColor(Color.WHITE);
				graphics.setFont(helpTitle);
				graphics.drawString("How To Play:", 55, 540);
				graphics.setFont(helpInfo);
				graphics.drawString("The goal of Earvin's RTS is to capture your opponents base before they capture", 55, 560);
				graphics.drawString("yours. Collect coins that are strewn around the board in order to use your special ability.", 55, 580);
				graphics.drawString("Your ability depends on which player you are. Player One gets to use firballs.",55, 600);
				graphics.drawString("Player Two gets to lay mines. If the opposing player is hit by these weapons, then", 55, 620);
				graphics.drawString("they die and respawn in the center. Player One's base is located at the top left and", 55, 640);
				graphics.drawString("Player Two's base is located at the bottom left. When you get to your opponents base,", 55, 660);
				graphics.drawString("step on the tile and wait for the progress bar to complete, and you have won.", 55, 680);
			}
		}
	}

	private class KeyMonitor implements Runnable
	{
		// these boolean values are made to help ensure that just in case something goes wrong we can correct it (in the while loop in the run method)
		
		/*PLAYER ONE*/
		private boolean wKeyDown = false;
		private boolean aKeyDown = false;
		private boolean dKeyDown = false;
		private boolean sKeyDown = false;
		
		/*PLAYER TWO*/
		private boolean upKeyDown = false;
		private boolean leftKeyDown = false;
		private boolean rightKeyDown = false;
		private boolean downKeyDown = false;
		
		public void run ()
			// Essentially we create input maps that are invoked when the fram that the 'canvas' component is on is focused. Each input map
			// is associated with a specific key which references a specific action map. that action map then calls a specific action which
			// does the action we want associated with a specific key.
		{
			/*PLAYER ONE*/
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "wKeyDown");	// up
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "aKeyDown");	// left
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "dKeyDown");	// right
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "sKeyDown");	// down
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "eKeyDown");	// fireball
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "qKeyDown");	// wall

			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "wKeyUp");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "aKeyUp");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "dKeyUp");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "sKeyUp");

			canvas.getActionMap().put("wKeyDown", wKey);
			canvas.getActionMap().put("aKeyDown", aKey);
			canvas.getActionMap().put("dKeyDown", dKey);
			canvas.getActionMap().put("sKeyDown", sKey);
			canvas.getActionMap().put("eKeyDown", eKey);
			canvas.getActionMap().put("qKeyDown", qKey);
			
			canvas.getActionMap().put("wKeyUp", wKeyReleased);
			canvas.getActionMap().put("aKeyUp", aKeyReleased);
			canvas.getActionMap().put("dKeyUp", dKeyReleased);
			canvas.getActionMap().put("sKeyUp", sKeyReleased);
			
			/*PLAYER TWO*/
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "upKeyDown");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "leftKeyDown");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "rightKeyDown");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "downKeyDown");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD0"), "zeroKeyDown"); // mines
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD1"), "oneKeyDown");	// walls
			
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "upKeyUp");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "leftKeyUp");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "rightKeyUp");
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "downKeyUp");

			canvas.getActionMap().put("upKeyDown", upKey);
			canvas.getActionMap().put("leftKeyDown", leftKey);
			canvas.getActionMap().put("rightKeyDown", rightKey);
			canvas.getActionMap().put("downKeyDown", downKey);
			canvas.getActionMap().put("zeroKeyDown", zeroKey);
			canvas.getActionMap().put("oneKeyDown", oneKey);
			
			canvas.getActionMap().put("upKeyUp", upKeyReleased);
			canvas.getActionMap().put("leftKeyUp", leftKeyReleased);
			canvas.getActionMap().put("rightKeyUp", rightKeyReleased);
			canvas.getActionMap().put("downKeyUp", downKeyReleased);

			while(true)
				// these if statements just check that in case a movement key is pressed down, but the player is not moving (for example if 'W' is pressed
				// but player one is not moving), then we move the player by setting direction here
			{
				/*PLAYER ONE*/
				if (wKeyDown && (canvas.playerOne.getDirection() == 0))
					canvas.playerOne.setDirection(1);
				if (aKeyDown && (canvas.playerOne.getDirection() == 0))
					canvas.playerOne.setDirection(2);
				if (dKeyDown && (canvas.playerOne.getDirection() == 0))
					canvas.playerOne.setDirection(3);
				if (sKeyDown && (canvas.playerOne.getDirection() == 0))
					canvas.playerOne.setDirection(4);
				
				/*PLAYER TWO*/
				if (upKeyDown && (canvas.playerTwo.getDirection() == 0))
					canvas.playerTwo.setDirection(1);
				if (leftKeyDown && (canvas.playerTwo.getDirection() == 0))
					canvas.playerTwo.setDirection(2);
				if (rightKeyDown && (canvas.playerTwo.getDirection() == 0))
					canvas.playerTwo.setDirection(3);
				if (downKeyDown && (canvas.playerTwo.getDirection() == 0))
					canvas.playerTwo.setDirection(4);
			}
		}
		
		// These actions are relatively self explanatory
		/*PLAYER ONE*/
		Action wKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				wKeyDown = true;
				canvas.playerOne.setDirection(1);
			}
		};

		Action aKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				aKeyDown = true;
				canvas.playerOne.setDirection(2);
			}
		};

		Action dKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				dKeyDown = true;
				canvas.playerOne.setDirection(3);
			}
		};

		Action sKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				sKeyDown = true;
				canvas.playerOne.setDirection(4);
			}
		};
		
		Action eKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
				// if the user has more or equal than 50 (dollars?) then the fireballs are created
				// the fireballs are created depending on which key player one is pressing. For example
				// if player one is pressing 'W' while pressing 'E' (fireball), then a fireball is created moving upwards
				// 50 (dollars?) is subtracting from player one's money count for each fireball made
			{
				if (canvas.playerOne.getMoney() >= 50)
				{
					if(wKeyDown)
					{
						canvas.fireballs.add(new Fireball(canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16], canvas.tiles, 0));
						canvas.playerOne.changeMoney(-50);
					}
					else if(aKeyDown)
					{
						canvas.fireballs.add(new Fireball(canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16], canvas.tiles, 1));
						canvas.playerOne.changeMoney(-50);
					}
					else if(dKeyDown)
					{
						canvas.fireballs.add(new Fireball(canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16], canvas.tiles, 2));
						canvas.playerOne.changeMoney(-50);
					}
					else if(sKeyDown)
					{
						canvas.fireballs.add(new Fireball(canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16], canvas.tiles, 3));
						canvas.playerOne.changeMoney(-50);
					}
				}
			}
		};

		Action qKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
				// each wall costs 2000 (dollars?).
				// we change the type of tile the player is currently on to 'W'
			{
				if (canvas.playerOne.getMoney() >= 2000 && !canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16].getOccupied())
				{
					canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16].setTile('W');
					canvas.playerOne.changeMoney(-2000);
				}
			}
		};
		
		Action wKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				wKeyDown = false;
				canvas.playerOne.setDirection(0);
			}
		};

		Action aKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				aKeyDown = false;
				canvas.playerOne.setDirection(0);
			}
		};

		Action dKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				dKeyDown = false;
				canvas.playerOne.setDirection(0);
			}
		};

		Action sKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				sKeyDown = false;
				canvas.playerOne.setDirection(0);
			}
		};
		
		/*PLAYER TWO*/
		Action upKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				upKeyDown = true;
				canvas.playerTwo.setDirection(1);
			}
		};

		Action leftKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				leftKeyDown = true;
				canvas.playerTwo.setDirection(2);
			}
		};

		Action rightKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				rightKeyDown = true;
				canvas.playerTwo.setDirection(3);
			}
		};

		Action downKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				downKeyDown = true;
				canvas.playerTwo.setDirection(4);
			}
		};
	
		Action zeroKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
				// Mines cost 50 (dollars?)
				// We first ensure that there are no mines already buried where player two is attempting to create a new mine
				// we add a new mine at player two position
				// we set the mine boolean to true
				// we subtract 50
			{
				if (canvas.playerTwo.getMoney() >= 50 && !canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16].getMine()) // Reduces money 
				{
					canvas.mines.add(new Mine(canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16]));
					canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16].setMine(true);
					canvas.playerTwo.changeMoney(-50);
				}
					
			}
		};
		
		Action oneKey = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
				// same thing with player one. the tile player two is on is converted to a 'W' tile if player two has more than or equal to 2000 (dollars?)
			{
				if (canvas.playerTwo.getMoney() >= 2000 && !canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16].getOccupied())
				{
					canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16].setTile('W');
					canvas.playerTwo.changeMoney(-2000);
				}
			}
		};
		
		Action upKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				upKeyDown = false;
				canvas.playerTwo.setDirection(0);
			}
		};

		Action leftKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				leftKeyDown = false;
				canvas.playerTwo.setDirection(0);
			}
		};

		Action rightKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				rightKeyDown = false;
				canvas.playerTwo.setDirection(0);
			}
		};

		Action downKeyReleased = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				downKeyDown = false;
				canvas.playerTwo.setDirection(0);
			}
		};
	}
}