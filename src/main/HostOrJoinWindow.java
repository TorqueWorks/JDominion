package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.apache.log4j.*;

import dominion.client.DominionClient;
import dominion.client.DominionClientProtocol;
import dominion.server.DominionServer;
import dominion.server.DominionServerProtocol;

import torque.client.TorqueNetworkClient;
import torque.server.TorqueNetworkServer;
public class HostOrJoinWindow extends JFrame implements ActionListener{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton mBHost = new JButton("Host");
	private JButton mBJoin = new JButton("Join");
	private JLabel mLIPAddr = new JLabel("IP Address");
	private JLabel mLPort = new JLabel("Port");
	private JTextField mTFIPAddr = new JTextField();
	private JTextField mTFPort = new JTextField();
	private static final int WINDOW_DEFAULT_WIDTH = 200;
	private static final int WINDOW_DEFAULT_HEIGHT = 200;
	
	public static void main(String[] args)
	{
		new HostOrJoinWindow();
	}
	
	public HostOrJoinWindow()
	{
		this.setTitle("JDominion");
		JPanel content = new JPanel(new BorderLayout());
		
		mBHost.setActionCommand("Host");
		mBHost.addActionListener(this);
		mBJoin.setActionCommand("Join");
		mBJoin.addActionListener(this);
		
		JPanel lBPanel = new JPanel(new GridLayout(2,1));
		lBPanel.add(mBHost);
		lBPanel.add(mBJoin);
		lBPanel.setPreferredSize(new Dimension(100,50));
		
		JPanel lIPanel = new JPanel(new GridLayout(2,2,0,10));
		lIPanel.add(mLIPAddr);
		lIPanel.add(mTFIPAddr);
		lIPanel.add(mLPort);
		lIPanel.add(mTFPort);
		lIPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0)); //pad the bottom
		lIPanel.setPreferredSize(new Dimension(200,75));
		content.add(lBPanel, BorderLayout.NORTH);
		content.add(lIPanel, BorderLayout.SOUTH);

		this.setContentPane(content);
		this.setPreferredSize(new Dimension(WINDOW_DEFAULT_WIDTH,WINDOW_DEFAULT_HEIGHT));
		this.setLocation(
				Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (WINDOW_DEFAULT_WIDTH / 2),
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - (WINDOW_DEFAULT_HEIGHT / 2));
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setVisible(true);
	}

	private boolean createServer(int aPort)
	{
		try {
			new DominionServer(aPort, new DominionServerProtocol());
		} catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @param aIPAddress
	 * @param aPort
	 * @return
	 */
	private boolean createClient(String aIPAddress, int aPort)
	{
		try {
			new DominionClient(aPort, aIPAddress, new DominionClientProtocol());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent a) {
		if(a.getActionCommand().equals("Host"))
		{
			mBHost.setEnabled(false);
			mBJoin.setEnabled(true);
			int lPort;
			try
			{
				lPort = Integer.parseInt(mTFPort.getText());
			}
			catch(NumberFormatException e)
			{
				//Let the user know the port was invalid
				return;
			}
			if(createServer(lPort))
			{ //We successfully created the server, now create the client for this user
				createClient("localhost", lPort);
				this.dispose(); //We have the server and client set up so we're all done here
			}
		}
		else if (a.getActionCommand().equals("Join"))
		{
			int lPort;
			try
			{
				lPort = Integer.parseInt(mTFPort.getText());
			}
			catch(NumberFormatException e)
			{
				//Let the user know the port was invalid
				return;
			}
			mBHost.setEnabled(true);
			mBJoin.setEnabled(false);
			if(createClient(mTFIPAddr.getText(), lPort))
			{ //The client was successfully created, we can dispose of this window now
				this.dispose();
			}
		}
		
	}
}
