// Pong.java
// Final Version 2008
// Created by Nick Savage and Bassam Saeed.

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class Pong extends JFrame implements KeyListener
{
	// Declaring a variety of variables that are used throughout the Pong class
	static final int WIDTH = 900;	// 900 pixels
	static final int HEIGHT = 350;	// 350 pixels
	static Graphics2D g2;			// Creates a Graphics2D variable. Used for drawing stuff

	static Random rand = new Random();
	static PaintSurface canvas;
	static Player player1 = new Player();	// Creates two players
	static Player player2 = new Player();
	
	Font winner = new Font("Verdana", Font.BOLD, 24);
	
	public static void main(String[] args)
	{
		new Pong(); // Calls the pong constructor
	}
	
	public Pong()
	{
		this.setSize(WIDTH, HEIGHT);		// Sets the width and height of the Frame
		this.setTitle("Project Pong");		// The title that appears on the top left
		this.setBackground(Color.BLACK);	// Sets the background colour as Black
		this.setResizable(false);		// now it can't be resized.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		canvas = new PaintSurface();
		
		canvas.setFocusable(true); // Automatically focuses on the Pong screen. No need to click into the window.
		canvas.addKeyListener(this); // Registers the KeyListener with the PaintSurface class
		
		this.add(canvas, BorderLayout.CENTER);
		this.setVisible(true);

		while(true)
		{
			canvas.repaint(); // Continously repaints the PaintSurface every 20 milliseconds. Double buffering is a default within Swing
			
			try
			{
				Thread.sleep(20);
			}
			catch (Exception e){}
			
			if (player1.getScore() >= 10 || player2.getScore() >= 10)
			{
				break; // Stops the program once any player reaches 10 points.
			}
		}
	}
	
	public void keyPressed (KeyEvent e)
	{
		int keyCode = e.getKeyCode();	// getKeyCode is a method that returns the int value of the key being pressed "e"

		if (keyCode == KeyEvent.VK_UP)
			canvas.paddleRight.changeYSpeed(-5); // Right paddle moves up by 5 pixels
		else if (keyCode == KeyEvent.VK_DOWN)
			canvas.paddleRight.changeYSpeed(5); // Right paddle moves down by 5 pixels
		if (keyCode == 87)
			canvas.paddleLeft.changeYSpeed(-5); // Left paddle moves up by 5 pixels
		else if (keyCode == 83)
			canvas.paddleLeft.changeYSpeed(5);	// Left paddle moves down by 5 pixels
	}
	
	public void keyReleased (KeyEvent e)
	{
		int keyCode = e.getKeyCode();	// this time, return the int value of the key being released "e"
		
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN)
			canvas.paddleRight.changeYSpeed(0); // Stops the right paddle from moving when the up or down key are let go
		if (keyCode == 87 || keyCode == 83)
			canvas.paddleLeft.changeYSpeed(0);	// Stops the left paddle from moving when the W or S keys are pressed
	}
	public void keyTyped (KeyEvent e){} // Because KeyListener is an Interface, the keyTyped method must be overloaded even though we're not using it.
	
	private class PaintSurface extends JComponent
	{
		Ball bar = new Ball(20);	// Creates a Ball
		
		Paddle paddleRight = new Paddle(100, Pong.WIDTH - 20, 20); // Creates the two paddles
		Paddle paddleLeft = new Paddle(100, 0, 20);
		
		{
			bar.setPaddles(paddleLeft, paddleRight);	// Basically just allows us easy access to the Paddles in the Ball class
		}
		
		public void paint(Graphics g)
		{
			g2 = (Graphics2D)g;
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);	// Done to look pretty
			
			
			// Calls the move methods in the Ball and Paddle classes
			bar.move();	
			paddleRight.move();
			paddleLeft.move();

			score();
			
			if (player1.getScore() >= 10)	// Shows if Player One has won
			{
				g2.setFont(winner);
				g2.drawString("Congratulations! Player One is the winner!", 150, 100);
			}
			else if (player2.getScore() >= 10)	// Shows if Player Two has won
			{
				g2.setFont(winner);
				g2.drawString("Congratulations! Player Two is the winner!", 150, 100);
			}
		}
		
		private void score()
		{
			g2.drawString("Player One: " + Pong.player1.getScore(), 200, 20);	// Updates regularly to show the score of Player One
			g2.drawString("Player Two: " + Pong.player2.getScore(), 615, 20);	// Updates regularly to show the score of Player Two
			
			g2.drawLine(0, 20, 200, 20);	// Just some lines to look nice
			g2.drawLine(700, 20, 900, 20);	// More lines
		}
		
	}
}

class Player
{
	// Best class ever.
	// This is a quick class that just updates each players score. The players are associated with the wall on the opposite of their paddle, and this is done in the Ball class.
	private int score;
	
	public void modScore(int score)
	{
		this.score += score;
	}
	
	public int getScore()
	{
		return score;
	}
}

class Ball extends Ellipse2D.Float
{
	private int x_speed, y_speed;
	private int radius;
	private int hitCounter;
	private Paddle paddleLeft, paddleRight; // Again for the easy access

	public Ball(int r)
	{
		super(Pong.WIDTH / 2 - r, Pong.HEIGHT / 2 - r, r, r); // Creates a ball with a radius of 20 in the middle of the screen
		
		this.radius = r;
		x_speed = Pong.rand.nextInt(5) + 5;	// Creates a random x speed for the ball
		y_speed = Pong.rand.nextInt(5) + 5;	// Creates a random y speed for the ball
	}
	
	public void restart(boolean wall)	// A method that simply resets the position and speed of the ball once it hits either of the two walls
	{
		hitCounter = 0;	// Resets the hit counter so that the ball doens't start too fast
		
		// Resets the position of the ball
		super.x = Pong.WIDTH / 2 - radius;
		super.y = Pong.HEIGHT / 2;
		
		// Basically what this if statement does is that is moves the ball at a random speed in the direction of the Player who scored
		if (wall)
		{
			x_speed = Pong.rand.nextInt(5) + 5;
			y_speed = Pong.rand.nextInt(5) + 5;
		}
		else
		{
			x_speed = -1 * (Pong.rand.nextInt(5) + 5);
			y_speed = -1 * (Pong.rand.nextInt(5) + 5);
		}

		try
		{
			Thread.sleep(1000); // Just a short delay between scores
		}
		catch (Exception e){}

	}
	
	public void move() // Ah, the most annoying method ever.
	{
		if (super.x <= 0)	// If the ball hits the left wall
		{	
			restart(true); // Resets the ball setting 'wall' to true so that the ball moves towars Player two
			Pong.player2.modScore(1);	// Adds one to Player Two's score
		}
		if (super.x >= Pong.WIDTH - radius)	// If the ball hits the right wall
		{
			// See above, but with right wall and player 1 and the opposite direction
			restart(false);
			Pong.player1.modScore(1);
		}
        if (super.y <= 0 || super.y >= Pong.HEIGHT - (radius * 2))
        {	
			y_speed = -y_speed;	// Simply causes the ball to bounce opposite to its original y direction when hitting the top or bottom wall
		}
		if (super.intersects(paddleLeft) || super.intersects(paddleRight))
		// The most annoying if statement ever. 'intersects' is a method in the Ellipse2D.Float class
		// This if statement checks to see if the ball hit either of the paddles
		{
			if (paddleLeft.returnTopLine().intersects(this.getBounds())
				|| paddleLeft.returnBottomLine().intersects(this.getBounds())
				|| paddleRight.returnTopLine().intersects(this.getBounds())
				|| paddleRight.returnBottomLine().intersects(this.getBounds()))
			// This if statement checks to see if the ball hit the top or bottom of either paddle, if it did, then the y speed (direction) is reversed.
			// In order to achieve this, we created 2 lines that correspond with the top and bottom of a paddle (in the Paddle class)  and then
			// we simply checked to see if the ball 'intersected' at any point of the line
			{
				y_speed = -y_speed;
			}
			
			// The following 2 if statements increase the x speed if the ball hit the paddle. This is done so that the game isn't too easy and doesn't last forever.
			// However, we had to place a cap on the speed to 5 (plus the original x and y speed generated) because otherwise the ball would go too fast and the collision wouldn't be detected.
			if (hitCounter < 5)
				hitCounter++;	// Increases the speed everytime teh paddle is hit
			
			// The x speed also changes when the ball hits the top or bottom of the paddle
			if (super.intersects(paddleLeft))  // x speed reverses if the ball hit the paddle anywhere except for the top or bottom.
				x_speed = -x_speed + hitCounter;
			else
				x_speed = -x_speed + -hitCounter;
		}
		super.x += x_speed;	// Continously adds the x speed to the x position of the ball
        super.y += y_speed;	// Continously adds the y speed to the y postion of the ball
		
		Pong.g2.setColor(Color.GREEN);
		Pong.g2.fill(this);
	}
	
	public void setPaddles (Paddle paddleLeft, Paddle paddleRight) // Again for the ease
	{
		this.paddleLeft = paddleLeft;
		this.paddleRight = paddleRight;
	}
}

class Paddle extends Rectangle2D.Float // This class is actually rather self explanatory but I'll comment it anyway
{
	private int y_speed = 0; // There is only a y speed because the paddles only move up and down.
	Line2D.Float lineTop, lineBottom;
	
	public Paddle(int h, int x, int w)
	{
		super(x, Pong.HEIGHT / 2 - (h / 2), w, h); 	// Creates a rectangle with the given parametres that were stated in the PaintSurface class. In our case, two are created.
	}
	
	public void changeYSpeed(int y_speed)
	{
		this.y_speed = y_speed; // A method made so that the KeyListener methods could easily manipulate the position of the paddles
	}
	
	public Line2D.Float returnTopLine()	// Creates a line at the very top of a paddle
	{
		lineTop = new Line2D.Float(super.x, super.y, super.x + super.width, super.y);
		return lineTop;	// The collision if statement uses this method to determine where the top of a paddle is and if it intersects with the ball
	}
	
	public Line2D.Float returnBottomLine() // Creates a line at the very bottom of a paddle
	{
		lineBottom = new Line2D.Float(super.x, super.y + super.height,
									  super.x + super.width, super.y + super.height);
		return lineBottom; // The collision if statement uses this method to determine where the bottom of a paddle is and if it intersects with the ball
	}
	
	public void move()
	{
		if (super.y < 0)
            super.y = 0; // Instead of the paddle bouncing off the edge, it stops.
		else if (super.y > Pong.HEIGHT - (height + (height / 4)))
			super.y = Pong.HEIGHT - (height + (height / 4)); // Same idea of the paddle stopping.

		super.y += y_speed; // The y position changes based of the y speed of the paddles which are manipulated by the KeyListener methods
							// For example, pressing the up key sets the y speed to 5 and so the y position is continously increasing by 5 until the key is let go
							// and the y speed is set as 0 and so the y position doens't increase or decrease.
		Pong.g2.setColor(Color.GREEN);
		Pong.g2.fill(this);
	}
}
