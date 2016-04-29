package randomtestfive.nodechat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

public class Coordinator implements Runnable 
{
	public MulticastSocket socket;
	private int latest;
	private String name;
	private InetAddress ip;
	private int port;
	private String last;
	private boolean starting = true;
	
	public Coordinator(String n) 
	{
		name = n;
	}
	
	@Override
	public void run() 
	{
		try {
			Connect(InetAddress.getByName("224.100.0.50"), 10000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(true)
		{
			byte[] buffer = new byte[512];
			DatagramPacket p;
			p = new DatagramPacket(buffer, buffer.length);
			try {
				socket.setSoTimeout(0);
				socket.receive(p);
				if(new String(p.getData()).startsWith("join"))
				{
					buffer = new byte[512];
					System.out.println("\"" + new String(p.getData()).split(" ")[1] + "\"" + " has joined.");
					buffer = ("here " + name).getBytes();
					last = "here " + name;
					p = new DatagramPacket(buffer, buffer.length, ip, port);
					socket.send(p);	
				}
				else if(last != null && !last.equals(new String(p.getData())) && !new String(p.getData()).startsWith("here"))
				{
					System.out.println(new String(p.getData()));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void Connect(InetAddress i, int po)
	{
		ip = i;
		port = po;
		byte[] buffer = new byte[512];
		buffer = ("join " + name).getBytes();
		DatagramPacket p;
		p = new DatagramPacket(buffer, buffer.length, ip, port);
		try 
		{
			socket = new MulticastSocket(port);
			socket.send(p);
			buffer = new byte[512];
			p = new DatagramPacket(buffer, buffer.length);
			socket.setSoTimeout(1500);
			socket.joinGroup(ip);
			socket.receive(p);
			if(new String(p.getData()).startsWith("here"))
			{
				boolean is = true;
				ArrayList<String> joined = new ArrayList<String>();
				joined.add(new String(p.getData()).split(" ")[1]);	
				while(starting)
				{	
					buffer = new byte[512];
					p = new DatagramPacket(buffer, buffer.length);
					socket.setSoTimeout(1000);
					try
					{
						socket.receive(p);
						joined.add(new String(p.getData()).split(" ")[1]);
						is = false;
					} 
					catch (IOException e) 
					{
						String out;
						if(is)
							out = list(joined) + "is online.";
						else
							out = list(joined) + "are online.";
						starting = false;
						socket.setSoTimeout(0);
						System.out.println(out);
					}
				}
			}
		} 
		catch (IOException e)
		{
			System.out.println("no nodes found");
			starting = false;
		}
	}
	
	public void Send(String message)
	{
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		byte[] buffer = new byte[512];
		buffer = ("[<" + currentTimestamp.toString().substring(11, 19) + "> " + name + " ]: " + message).getBytes();
		DatagramPacket p = new DatagramPacket(buffer, buffer.length, ip, port);
		try {
			socket.send(p);
			last = new String(p.getData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String list(ArrayList<String> in)
	{
		String out;
		if(in.size() <= 2)
			out = in.get(0) + " ";
		else
			out = in.get(0) + ", ";
		for(int i = 1; i < in.size(); i++)
		{
			if(i != in.size() - 1 && in.size() > 2)
			{
				out = out + in.get(i) + ", ";
			}
			else
			{
				out = out + "and " + in.get(i) + " "; 
			}
		}
		return out;
	}
}
