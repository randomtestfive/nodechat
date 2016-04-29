package randomtestfive.nodechat;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main 
{
	public static Visual v;
	private static Coordinator c;
	private static String name;
	
	public static void main(String[] args) 
	{
		v = new Visual("v0.0.1");
		System.out.print("Enter username: ");
		name = v.getLine(false);
		c = new Coordinator(name);
		Thread t = new Thread(c);
		t.start();
		while(true)
		{
			String m = v.getLine(true);
			c.Send(m);
		}
	}

}
