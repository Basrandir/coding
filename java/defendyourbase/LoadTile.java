import java.io.*;
import javax.swing.*;
import java.awt.*;

public class LoadTile
{
    private String line;	// These two String variables store the line of the file that the map is read from
    private String check;	// This variable is used to determine when null is reached
    
    private Tile[][] tiles;
    private boolean map = true;
    private int amount = 0;	// Amount of lines in the map file
    private int x, y;
    private ImageIcon grass, ramp, wall, tree, mineral, playerOneBase, playerTwoBase;    
   
    private File file;
    private BufferedReader in, work;

    public LoadTile()
    {
	if (CaptureBase.operatingSystem.contains("Windows"))
	{
	    grass = new ImageIcon ("images\\grass.png");
	    ramp = new ImageIcon ("images\\ramp.png");
	    wall = new ImageIcon ("images\\wall.png");
	    tree = new ImageIcon ("images\\tree.png");
	    mineral = new ImageIcon ("images\\coin.gif");
	    playerOneBase = new ImageIcon ("images\\base1.png");
	    playerTwoBase = new ImageIcon ("images\\base2.png");
	}
	else
	{
	    grass = new ImageIcon ("images/grass.png");
	    ramp = new ImageIcon ("images/ramp.png");
	    wall = new ImageIcon ("images/wall.png");
	    tree = new ImageIcon ("images/tree.png");
	    mineral = new ImageIcon ("images/coin.gif");
	    playerOneBase = new ImageIcon ("images/base1.png");
	    playerTwoBase = new ImageIcon ("images/base2.png");
	}

	try
	    // creates epic file streams
	{
	    file = new File ("map1.txt");
	    in = new BufferedReader(new FileReader(file));
	    work = new BufferedReader(new FileReader(file));
	}
	catch(IOException e){}
	
	try
        {
	    while(true)
		    // The point of this is to determine the 'amount' of lines in the map file, hence the variable name.
		    // Unfortunately, I have no idea how to not store null into 'line' and then use it to create the tile array so I resorted to using another variable called 'check'.
	    {
		if((line = work.readLine()) == null)
		    break; // Breaks when end of file is reached
		else
		    amount++; // Increments amounts for each line read
		
		check = line; // Sets check to line, check is never set to null because the program breaks out of the while loop when line is set to null
	    }
	    work.close(); // Close stream
	}
	catch(IOException e)
	{
		// I really should set up a little check system that ensures that a map file exists and that there's stuff written in it.
	}

	tiles = new Tile[check.length()][amount];
	loadMap();
    }
    
    public void loadMap()
    {
	for(int y = 0 ; y < this.amount ; y++)
	{
	    try
	    {
		line = in.readLine();
	    }
	    catch(IOException e){}
		
	    for(int x = 0 ; x < line.length() ; x++)
		tiles[x][y] = new Tile(x, y, line.charAt(x), this); // Assigns the character value found in the map file (T, W, G, R, etc.) to tiles array where x and y represent the position of the character in number of tiles
	}
    }
	
    public Tile[][] getTiles()
    {
	return this.tiles;
    }
    
    public void setTiles(int x, int y, char value)
    {
	tiles[(x - 16) / 16][y / 16].setType(value);
    }
    
    public String getLine()
    {
	return this.line;
    }
    
    public int getAmount()
    {
	return this.amount;
    }
    
    public Image getGrass()
    {
	return this.grass.getImage();
    }

    public Image getTree()
    {
	return this.tree.getImage();
    }

    public Image getRamp()
    {
	return this.ramp.getImage();
    }

    public Image getWall()
    {
	return this.wall.getImage();
    }

    public Image getMineral()
    {
	return this.mineral.getImage();
    }

    public Image getPlayerOneBase()
    {
	return this.playerOneBase.getImage();
    }

    public Image getPlayerTwoBase()
    {
	return this.playerTwoBase.getImage();
    }

    

}