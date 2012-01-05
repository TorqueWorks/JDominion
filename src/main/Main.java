package main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import torque.server.*;
import torque.client.*;

public class Main {


	public static void main(String[] args) {
		TorqueNetworkServer server = null;
		try {
			server = new TorqueNetworkServer(1337, new TextCallback("ServerSocket"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.exit(1);
		}
		
		TorqueNetworkClient client = null;
		TorqueNetworkClient client2 = null;
		try {
			client = new TorqueNetworkClient(1337, "192.168.2.6", new TextCallback("Client1Socket"));
			client2 = new TorqueNetworkClient(1337, "192.168.2.6", new TextCallback("Client2Socket"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			client.sendMessage("Hello there2");
			client.sendMessage("Testing");
			client2.sendMessage("NO U");
			server.sendMessage("oh hai");
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}
