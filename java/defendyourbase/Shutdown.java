import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Shutdown extends JFrame implements ActionListener
{
    public Shutdown(String player)
    {
    	this.setSize(400, 400);
    	this.setTitle("Shutdown");
    	
    	JPanel panel = new JPanel();
    	JLabel label = new JLabel(player.toUpperCase() + " WINS!");
    	panel.add(label);

    	JButton shutdown = new JButton("Shutdown");
    	shutdown.addActionListener(this);
    	panel.add(shutdown);
    
    	JButton restart = new JButton("Restart");
    	restart.addActionListener(this);
    	panel.add(restart);
    	this.add(panel);
    	
    	this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e)
    {
    	String command = e.getActionCommand();
    	
    	if (command.equals("Shutdown"))
    		System.exit(0);
    	if (command.equals("Restart"))
    		;
    }
}