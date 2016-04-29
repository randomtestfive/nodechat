package randomtestfive.nodechat;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
*
* @author CMAN Team
*/
@SuppressWarnings("serial")
public class Visual extends JFrame
{
	final List<String> holder = new LinkedList<String>();
	JTextField text = new JTextField("");
	JTextArea area = new JTextArea(25, 55);
	JScrollPane scroll = new JScrollPane(area);
	private String out = "";
	private ArrayList<String> old = new ArrayList<String>();
	int pos;
	
	public String getLine(boolean quiet)
	{	    
		synchronized (holder)
		{
			// wait for input from field
			while (holder.isEmpty())
			{
				try 
				{
					holder.wait();
				} 
				catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
			}
			out = holder.remove(0);
			old.add(text.getText());
			this.text.setText("");
			if(!quiet)
			System.out.println(out);
		}
		return out;
	}
	
	public Visual(String version)
	{
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		System.setOut(new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException 
			{
				area.append(String.valueOf((char)b));
				pos = old.size();
				area.setCaretPosition(area.getDocument().getLength());
			}
		}));
		
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroll);
		this.add(text);
		this.area.setEditable(false);
		text.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) 
			{
				synchronized (holder)
				{
					holder.add(text.getText());
					holder.notify();
				}
			}
		});	
		
		this.pack();
		this.setResizable(false);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int desktopwidth = gd.getDisplayMode().getWidth();
		int desktopheight = gd.getDisplayMode().getHeight();
		this.setLocation((desktopwidth/2)-(this.getWidth()/2), (desktopheight/2)-(this.getHeight()/2));
		this.setTitle("NodeChat " + version);
		try 
		{
			Image icon = ImageIO.read(getClass().getResource("nodechaticon.png"));
			new ImageIcon(icon);
			this.setIconImage(icon);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}
