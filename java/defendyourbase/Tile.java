import javax.swing.*;
import java.awt.*;

public class Tile
{
    private boolean occupied;
    private int x, y;
    private int posX, posY;
    private char type;
    private LoadTile tileLoader;
    Image img;
    
    public Tile (int x, int y, char type, LoadTile tileLoader)
    {
	this.tileLoader = tileLoader;
	this.x = x;
	this.y = y;
	this.posX = x * 16;
	this.posY = y * 16;
	this.type = type;
		
	if (type == 'T')  // Image tile for trees
	    this.img = tileLoader.getTree();
	else if (type == 'W')  // Image tile for walls
	    this.img = tileLoader.getWall();
	else if (type == 'M')  // Image tile for minerals
	    this.img = tileLoader.getMineral();
	else if (type == 'R')  // Image tile for ramps
	    this.img = tileLoader.getRamp();
	else if (type == '0') 
	    this.img = tileLoader.getGrass();
	else if (type == '1')  // Image tile for player one base
	    this.img = tileLoader.getPlayerOneBase();
	else if (type == '2')  // Image tile for player two base
	    this.img = tileLoader.getPlayerTwoBase();
	if (type == 'T' || type == 'W')
	    occupied = true;		
	else
	    occupied = false;   
    }
    
    public void setType (char type)
    {
	this.type = type;
    }
    
    public char getType()
    {
	return type;
    }
    
    public void setOccupied(boolean occupied)
    {
		this.occupied = occupied;
    }
    
    public boolean getOccupied()
    {
	return occupied;
    }
    
    public void setX(int x)
    {
	this.x = x;
    }
    
    public void setY(int y)
    {
	this.y = y;
    }
    
    public void setPosX(int posX)
    {
	this.posX = posX;
    }
    
    public void setPosY(int posY)
    {
	this.posY = posY;
    }
    
    public int getX()
    {
	return x;
    }
    
    public int getY()
    {
	return y;
    }
    
    public int getPosX()
    {
	return posX;
    }
    
    public int getPosY()
    {
	return posY;
    }
    
    public void setTile(char type)
    {
		if (this.type == '0')
		{
		    this.type = type;
		    
		    if (type == 'T')
			this.img = tileLoader.getTree();
		    else if (type == 'W')
			this.img = tileLoader.getWall();
		    else if (type == '0')
			this.img = tileLoader.getGrass();
		    
		    if (type == 'W' || type == 'T')
			occupied = true;
		    else
			occupied = false;	
		}
		else if (this.type == 'M')
		{
		    this.type = type;
		    this.img = tileLoader.getGrass();
		}
		else if (this.type == 'W')
		{
			this.type = type;
		    this.img = tileLoader.getGrass();	
		}	
    }
}