public class Player
{
	private int xPos, yPos;
	private int newX, newY;

	private boolean wall = false;
	private boolean increase = true;

	private String name;

	private int size = 16;
	private int money = 0;
	private int direction = 0;
	private int progress = 0;

	private Tile tile, newTile;
	private Tile[][] tiles;

	public Player (String name, Tile tile, Tile[][] tiles)
	{
		this.name = name;
		this.tile = tile;
		this.tiles = tiles;
		this.xPos = tile.getPosX();
		this.yPos = tile.getPosY();
		this.money = money;
	}

	public void move(int direction)
		// manipulates player's position based on what keys the user pressed
	{
		newX = 0;
		newY = 0;

		if (direction == 1) // w + up
			newY = -1;
		else if (direction == 2) // a + left
			newX = -1;
		else if (direction == 3) // d + right
			newX = 1;
		else if (direction == 4) // s + down
			newY = 1;

		newTile = tiles[(int)xPos / 16 + newX][(int)yPos / 16 + newY];

		if (!newTile.getOccupied())
			tile = newTile;

		xPos = tile.getPosX();
		yPos = tile.getPosY();
	}

	public int getXPos() // Gets x position
	{
		return this.xPos;
	}

	public int getYPos() // Gets y position
	{
		return this.yPos;
	}

	public int getDirection()
	{
		return this.direction;
	}

	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	public int getMoney()
	{
		return this.money;
	}

	public void changeMoney(int m)
	{
		this.money += m;
	}

	public int getSize()
	{
		return this.size;
	}

	public void setTile(Tile tile)
	{
		this.tile = tile;
		this.xPos = tile.getPosX();
		this.yPos = tile.getPosY();
	}

	public void setProgress(int progress)
	{
		this.progress = progress;
	}

	public void increaseProgress(int progress)
	{
		this.progress += progress;
	}

	public int getProgress()
	{
		return this.progress;
	}

	public void setIncrease (boolean increase)
	{
		this.increase = increase;
	}

	public boolean getIncrease()
	{
		return this.increase;
	}

	public String getName()
	{
		return this.name;
	}
}