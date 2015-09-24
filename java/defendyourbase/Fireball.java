import javax.swing.*;
import java.awt.*;

public class Fireball
{
	private int xPos;
	private int yPos;
	private int direction;

	private boolean end = false;

	private Tile tile, newTile;
	private Tile[][] tiles;

	public Fireball (Tile tile, Tile[][] tiles, int direction)
	{
		this.tile = tile;
		this.tiles = tiles;
		this.xPos = tile.getPosX();
		this.yPos = tile.getPosY();
		this.direction = direction;
	}

	public void move()
	{
		int newX = 0;
		int newY = 0;

		if (this.direction == 0)
			newY = -1;
		else if (this.direction == 1)
			newX = -1;
		else if (this.direction == 2)
			newX = 1;
		else if (this.direction == 3)
			newY = 1;

		newTile = tiles[(int)xPos / 16 + newX][(int)yPos / 16 + newY];

		if(!newTile.getOccupied())
			tile = newTile;
		else
			end = true;

		xPos = tile.getPosX();
		yPos = tile.getPosY();
	}

	public void flipDirection()
	{
		if(this.direction == 0)
			this.direction = 3;
		else if(this.direction == 1)
			this.direction = 2;
		else if(this.direction == 2)
			this.direction = 1;
		else if(this.direction == 3)
			this.direction = 0;

		this.end = !this.end;
	}

	public boolean getEnd()
	{
		return this.end;
	}

	public int getXPos()
	{
		return this.xPos;
	}

	public int getYPos()
	{
		return this.yPos;
	}
}