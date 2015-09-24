import java.util.ArrayList;
import javax.swing.*;		// Import statements for basic graphics implementation
import java.awt.event.*;
import java.awt.*;

public class CaptureBase extends JFrame implements MouseListener, KeyListener
{
	private static Graphics2D graphics;
	private static PaintSurface canvas;
	private static End finish;
	private static Start begin;
	static String operatingSystem;

	protected static final int JWIDTH = 1024;
	protected static final int JHEIGHT = 768;

	private static int DEFAULT_FPS = 30;
	private volatile boolean running = true;
	private volatile boolean starting = true;
	private volatile boolean finishing = false;
	private volatile boolean breakWall = false;

	private volatile String winner;
	private String playerOneName = "";
	private String playerTwoName = "";
	
	private KeyMonitor keyMonitor;
	private Thread keyBinding;

	public static void main (String[] args)
	{
		operatingSystem = System.getProperty("os.name");
		new CaptureBase(1000 / DEFAULT_FPS * 1000000);
	}

    public CaptureBase(long period)
    {
    	this.setSize(JWIDTH, JHEIGHT);
    	this.setTitle("Capture Base");
		this.setResizable(true);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		while(true)
		{
			begin = new Start();
			finish = new End();

			begin.setFocusable(true);
			begin.addMouseListener(this);
			begin.addKeyListener(this);
			
			this.add(begin);
			this.setVisible(true);
			
			while(starting)
			{
				begin.repaint();
			}

			this.remove(begin);

			canvas = new PaintSurface();
			canvas.setFocusable(true); // sets the canvas focusable

			keyMonitor = new KeyMonitor();
			keyBinding = new Thread(keyMonitor);
			keyBinding.start();

			this.add(canvas);
			this.setVisible(true);	// Allows user to see the game

			long beforeTime, afterTime, timeDiff, sleepTime;
			int noDelays = 0;

			beforeTime = System.currentTimeMillis();

			while(running)
			{
				canvas.repaint();

				afterTime = System.currentTimeMillis();
				timeDiff = afterTime - beforeTime;
				sleepTime = period - timeDiff;

				if (sleepTime > 0)
				{
					try
					{
						Thread.sleep(sleepTime/1000000);
					}
					catch (InterruptedException e){}
				}
				else
					Thread.yield();

				beforeTime = System.currentTimeMillis();
			}

			this.remove(canvas);

			finish.setFocusable(true);
			finish.addMouseListener(this);
			
			this.add(finish);
			this.setVisible(true);
			
			finishing = true;
			while(finishing){}
			this.remove(finish);
		}
    }
	
	public void mouseClicked(MouseEvent e)
	{
		int xPos = e.getX();
		int yPos = e.getY();
		
		System.out.println("x: " + xPos + ", y: " + yPos);
		if(starting)
		{
			begin.playerOneCursor = false;
			begin.playerTwoCursor = false;
			if (55 <= xPos && 235 >= xPos && 294 <= yPos && 320 >= yPos)
				begin.playerOneCursor = true;
			else if (55 <= yPos && 235 >= xPos && 380 <= yPos && 405 >= yPos)
				begin.playerTwoCursor = true;
				
			if (775 <= xPos && 830 >= xPos && 253 <= yPos && 267 >= yPos)
			{
				if(playerOneName.isEmpty())
					playerOneName = "player one";
				if(playerTwoName.isEmpty())
					playerTwoName = "player two";
				starting = false;
			}
		}
		
		if(finishing)
		{
			if (50 <= xPos && 160 >= xPos && 190 <= yPos && 215 >= yPos)
				finishing = false;
			else if (50 <= xPos && 130 >= xPos && 230 <= yPos && 250 >= yPos)
				System.exit(0);
		}
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	
	public void keyPressed (KeyEvent e)
	{
		char keyChar = e.getKeyChar();
		int keyCode = e.getKeyCode();

		if (begin.playerOneCursor)
		{
			if (playerOneName.length() > 0 && keyCode == 8)
				playerOneName = playerOneName.substring(0, playerOneName.length() - 1);
			else if (keyCode != 8 && begin.fm.stringWidth(playerOneName) <= 175)
				playerOneName += keyChar;
		}
		else if (begin.playerTwoCursor)
		{
			if (playerTwoName.length() > 0 && keyCode == 8)
				playerTwoName = playerTwoName.substring(0, playerTwoName.length() - 1);
			else if (keyCode != 8 && begin.fm.stringWidth(playerTwoName) <= 175)
				playerTwoName += keyChar;
		}
	}
	public void keyReleased (KeyEvent e){}
	public void keyTyped (KeyEvent e){}

	private class PaintSurface extends JComponent
	{
		LoadTile tileLoader = new LoadTile();
		Tile[][] tiles = tileLoader.getTiles();

		Player playerOne = new Player(playerOneName, tiles[26][17], tiles);
		Player playerTwo = new Player(playerTwoName, tiles[36][17], tiles);
		
		ArrayList<Fireball> fireballs = new ArrayList<Fireball>(0);
		ArrayList<Mine> mines = new ArrayList<Mine>(0);

		public void paint (Graphics g)
		{
			graphics = (Graphics2D)g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			playerOne.move(playerOne.getDirection());
			playerTwo.move(playerTwo.getDirection());

			for(int y = 0 ; y < tileLoader.getAmount() ; y++) // the amount of tiles on the y-axis
			{
				for(int x = 0 ; x < tileLoader.getLine().length() ; x++) // the amount of tiles on the x-axis
					graphics.drawImage(tiles[x][y].img, x * 16, y * 16, this);
			}
			
			graphics.setColor(Color.CYAN);
			graphics.fillOval(playerOne.getXPos(), playerOne.getYPos(), playerTwo.getSize(), playerOne.getSize());  // Draws player one
			graphics.drawString("Money: " + playerOne.getMoney(), playerOne.getXPos() - 25, playerOne.getYPos() + 25);  // Draw money count for player one
			graphics.setColor(Color.GREEN);
			graphics.fillOval(playerTwo.getXPos(), playerTwo.getYPos(), playerTwo.getSize(), playerTwo.getSize());  // Draws player two
			graphics.drawString("Money: " + playerTwo.getMoney(), playerTwo.getXPos() - 25, playerTwo.getYPos() + 25);  // Draw money count for player two

			graphics.setColor(Color.RED);
			for (int i = 0 ; i < fireballs.size() ; i++)
			{
				graphics.fillOval(fireballs.get(i).getXPos(), fireballs.get(i).getYPos(), 16, 16);
				fireballs.get(i).move();
				if(fireballs.get(i).getEnd())
					fireballs.get(i).flipDirection();
				
				if(fireballs.get(i).getXPos() == playerTwo.getXPos() && fireballs.get(i).getYPos() == playerTwo.getYPos())
				{
					fireballs.remove(i);
					playerTwo.setTile(tiles[36][17]);
				}
			}
			
			graphics.setColor(Color.BLUE);
			for (int i = 0 ; i < mines.size() ; i++)
			{
				if (mines.get(i).getCounter() < 50)
				{
					graphics.fillOval(mines.get(i).getXPos(), mines.get(i).getYPos(), 16, 16);
					mines.get(i).incrementCounter();
				}
				
				if(mines.get(i).getXPos() == playerOne.getXPos() && mines.get(i).getYPos() == playerOne.getYPos())
				{
					mines.remove(i);
					playerOne.setTile(tiles[26][17]);
				}
			}

			if ((tiles[playerOne.getXPos() / 16][playerOne.getYPos() / 16].getType()) == '2')
			{
				graphics.setColor(Color.CYAN);
				graphics.drawRect(playerOne.getXPos() + 8, playerOne.getYPos() - 16, 64, 8);
				graphics.fillRect(playerOne.getXPos() + 8, playerOne.getYPos() - 16, playerOne.getProgress(), 8);
				graphics.drawString("Timer: ", playerOne.getXPos() - 32, playerOne.getYPos() - 8);
				
				if (playerOne.getIncrease())
					playerOne.increaseProgress(1);
					
				playerOne.setIncrease(!playerOne.getIncrease());
				
				if (playerOne.getProgress() == 65)
				{
					running = false;
					winner = playerOne.getName();
				}
			}
			else
				playerOne.setProgress(0);

			if ((tiles[playerTwo.getXPos() / 16][playerTwo.getYPos() / 16].getType()) == '1')
			{
				graphics.setColor(Color.GREEN);
				graphics.drawRect(playerTwo.getXPos() + 8, playerTwo.getYPos() - 16, 44, 8);
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
			
			if (breakWall == true)
			{
				graphics.setColor(Color.CYAN);
				graphics.drawRect(playerOne.getXPos() + 8, playerOne.getYPos() - 16, 64, 8);
				graphics.fillRect(playerOne.getXPos() + 8, playerOne.getYPos() - 16, playerOne.getProgress(), 8);
				graphics.drawString("Timer: ", playerOne.getXPos() - 32, playerOne.getYPos() - 8);
				
				if (playerOne.getIncrease())
					playerOne.increaseProgress(1);
					
				playerOne.setIncrease(playerOne.getIncrease());
				
				if (playerOne.getProgress() == 65)
				{		
					tiles[(playerOne.getXPos() / 16) - 1][(playerOne.getYPos() / 16) - 1].setTile('0');		
					tiles[(playerOne.getXPos() / 16)][(playerOne.getYPos() / 16) - 1].setTile('0');
					tiles[(playerOne.getXPos() / 16) + 1][(playerOne.getYPos() / 16) - 1].setTile('0');
					tiles[(playerOne.getXPos() / 16) - 1][(playerOne.getYPos() / 16)].setTile('0');
					tiles[(playerOne.getXPos() / 16) + 1][(playerOne.getYPos() / 16)].setTile('0');
					tiles[(playerOne.getXPos() / 16) - 1][(playerOne.getYPos() / 16) + 1].setTile('0');
					tiles[(playerOne.getXPos() / 16)][(playerOne.getYPos() / 16) + 1].setTile('0');
					tiles[(playerOne.getXPos() / 16) + 1][(playerOne.getYPos() / 16) + 1].setTile('0');
					playerOne.changeMoney(-100);	
				}
				breakWall = false;
			}
			
			// Allows player one to collect resources and earn money	
			if ((tiles[playerOne.getXPos() / 16][playerOne.getYPos() / 16].getType()) == 'M')
			{
				playerOne.changeMoney(50);
				tiles[playerOne.getXPos() / 16][playerOne.getYPos() / 16].setTile('0');
			}
			// Allows player two to collect resources and earn money
			if ((tiles[playerTwo.getXPos() / 16][playerTwo.getYPos() / 16].getType()) == 'M')
			{
				playerTwo.changeMoney(50);
				tiles[playerTwo.getXPos() / 16][playerTwo.getYPos() / 16].setTile('0');
			}
		}
	}

	private class End extends JPanel
	{
		private Image endScreen;

		public End()
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
			
			graphics.setFont(new Font("Arial Black", Font.BOLD, 48));
			graphics.drawImage(endScreen, 0, 0, this);
			graphics.drawString(winner + " wins!", 55, 125);
		}
	}
	
	private class Start extends JPanel
	{
		private boolean help = false;
		private boolean playerOneCursor = false;
		private boolean playerTwoCursor = false;

		FontMetrics fm;
		private Image startScreen;
			
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
			
			fm = graphics.getFontMetrics();

			graphics.drawImage(startScreen, 0, 0, this);
			graphics.drawString(playerOneName, 55, 315);
			graphics.drawString(playerTwoName, 55, 400);
			
			if(help)
			{
				graphics.drawString("",0,0);
			}
		}
	}

	private class KeyMonitor implements Runnable
	{
		private boolean wKeyDown = false;
		private boolean aKeyDown = false;
		private boolean dKeyDown = false;
		private boolean sKeyDown = false;
		private boolean eKeyDown = false;
		private boolean qKeyDown = false;
		
		private boolean upKeyDown = false;
		private boolean leftKeyDown = false;
		private boolean rightKeyDown = false;
		private boolean downKeyDown = false;
		private boolean zeroKeyDown = false;
		private boolean oneKeyDown = false;
		
		public void run ()
		{
			/*PLAYER ONE*/
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "wKeyDown");	// up
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "aKeyDown");	// left
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "dKeyDown");	// right
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "sKeyDown");	// down
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "eKeyDown");	// walls
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "qKeyDown");	// fireballs
			
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
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD0"), "zeroKeyDown");	// walls
			canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD1"), "oneKeyDown");	// mines
			
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
		
		Action eKey = new AbstractAction()  // Creates the walls when key is pressed
		{
			public void actionPerformed(ActionEvent e)
			{
				eKeyDown = true;
				if (canvas.tiles[(canvas.playerOne.getXPos() / 16) - 1][(canvas.playerOne.getYPos() / 16) - 1].getOccupied() ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16)][(canvas.playerOne.getYPos() / 16) - 1].getOccupied() ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16) + 1][(canvas.playerOne.getYPos() / 16) - 1].getOccupied() ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16) - 1][(canvas.playerOne.getYPos() / 16)].getOccupied() ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16) + 1][(canvas.playerOne.getYPos() / 16)].getOccupied()  ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16) - 1][(canvas.playerOne.getYPos() / 16) + 1].getOccupied() ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16)][(canvas.playerOne.getYPos() / 16) + 1].getOccupied() ||
						canvas.tiles[(canvas.playerOne.getXPos() / 16) + 1][(canvas.playerOne.getYPos() / 16) + 1].getOccupied() )
				{
					breakWall = true;	
				}		
				else if (canvas.playerOne.getMoney() >= 50 && !canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16].getOccupied())
				{
					canvas.tiles[canvas.playerOne.getXPos() / 16][canvas.playerOne.getYPos() / 16].setTile('W');
					canvas.playerOne.changeMoney(-50);
				}
			}
		};
		
		Action qKey = new AbstractAction() // Player one weapon
		{
			public void actionPerformed(ActionEvent e)
			{
				qKeyDown = true;

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
	
		Action zeroKey = new AbstractAction() // Creates the walls when key is pressed
		{
			public void actionPerformed(ActionEvent e)
			{
				zeroKeyDown = true;
				if (canvas.playerTwo.getMoney() >= 50 && !canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16].getOccupied())
				{
					canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16].setTile('W');
					canvas.playerTwo.changeMoney(-50);
				}
			}
		};
	
		Action oneKey = new AbstractAction() // Player two weapon
		{
			public void actionPerformed(ActionEvent e)
			{	
				oneKeyDown = true;
				if (canvas.playerTwo.getMoney() >= 50) // Reduces money 
				{
					canvas.mines.add(new Mine(canvas.tiles[canvas.playerTwo.getXPos() / 16][canvas.playerTwo.getYPos() / 16]));
					canvas.playerTwo.changeMoney(-50);
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