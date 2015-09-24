public class Mine
{
	private int xPos;
	private int yPos;
	private int counter = 0;

	public Mine(Tile tile)
	{
		this.xPos = tile.getPosX();
		this.yPos = tile.getPosY();
	}
	
	public int getCounter()
	{
		return this.counter;
	}
	
	public void incrementCounter()
	{
		this.counter++;
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