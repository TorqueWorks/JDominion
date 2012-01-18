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
import javax.swing.JOptionPane;
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
	
	public static final String TOKEN_TITLE = "JDominion";
	public static final String TOKEN_HOST = "Host";
	public static final String TOKEN_JOIN = "Join";
	public static final String TOKEN_NAME = "Name";
	public static final String TOKEN_IP_ADDRESS = "IP Address";
	public static final String TOKEN_PORT = "Port";
	public static final String TOKEN_INVALID_PORT = "The port was invalid. \nValid ports are between 1024 and 65535.";
	public static final String TOKEN_NO_NAME = "You must enter a name";
	private static final String ACTION_JOIN = "JOIN";
	private static final String ACTION_HOST = "HOST";
	
	private JButton mBHost = new JButton(TOKEN_HOST);
	private JButton mBJoin = new JButton(TOKEN_JOIN);
	private JLabel mLName = new JLabel(TOKEN_NAME);
	private JLabel mLIPAddr = new JLabel(TOKEN_IP_ADDRESS);
	private JLabel mLPort = new JLabel(TOKEN_PORT);
	private JTextField mTFName = new JTextField();
	private JTextField mTFIPAddr = new JTextField();
	private JTextField mTFPort = new JTextField();
	private static final int WINDOW_DEFAULT_WIDTH = 200;
	private static final int WINDOW_DEFAULT_HEIGHT = 200;
	
	public static void main(String[] args)
	{
		new HostOrJoinWindow();
	}
	
	/**
	 * Creates a new window which gives the options to either create or host a game of JDominion.
	 */
	public HostOrJoinWindow()
	{
		this.setTitle(TOKEN_TITLE);
		this.setPreferredSize(new Dimension(WINDOW_DEFAULT_WIDTH,WINDOW_DEFAULT_HEIGHT));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		initComponents();
		this.pack();
		mTFName.requestFocusInWindow();
		this.setVisible(true);
	}

	private void initComponents()
	{
		JPanel lContent = new JPanel(new BorderLayout());
		
		mBHost.setActionCommand(ACTION_HOST);
		mBHost.addActionListener(this);
		mBJoin.setActionCommand(ACTION_JOIN);
		mBJoin.addActionListener(this);
		
		JPanel lBPanel = new JPanel(new GridLayout(2,1));
		lBPanel.add(mBHost);
		lBPanel.add(mBJoin);
		lBPanel.setPreferredSize(new Dimension(100,50));
		
		JPanel lIPanel = new JPanel(new GridLayout(3,2,0,10));
		lIPanel.add(mLName);
		lIPanel.add(mTFName);
		lIPanel.add(mLIPAddr);
		lIPanel.add(mTFIPAddr);
		lIPanel.add(mLPort);
		lIPanel.add(mTFPort);
		lIPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0)); //pad the bottom
		lIPanel.setPreferredSize(new Dimension(200,100));
		lContent.add(lBPanel, BorderLayout.NORTH);
		lContent.add(lIPanel, BorderLayout.SOUTH);

		this.setContentPane(lContent);
	}
	/**
	 * Creates a server using the specified port as the Server Port
	 * @param aPort The port to accept connections on
	 * @return <code>TRUE</code> if the server was successfully created, <code>FALSE</code> if it wasn't
	 */
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
	 * Creates a client which will attempt to connect to the server at the specified IP and port.
	 * 
	 * @param aName The name to be displayed for this client
	 * @param aIPAddress The IP address/Hostname of the server to connect to
	 * @param aPort The port the server is listening on
	 * @param aIsAdmin Whether this client is an admin for the game or not
	 * @return <code>TURE</code> if the client successfully connected to the server, <code>FALSE</code> if not
	 */
	private boolean createClient(String aName, String aIPAddress, int aPort, boolean aIsAdmin)
	{
		try {
			new DominionClient(aPort, aIPAddress, new DominionClientProtocol(), aIsAdmin, aName);
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
		if(a.getActionCommand().equals(ACTION_HOST))
		{
			int lPort;
			try
			{
				lPort = Integer.parseInt(mTFPort.getText());
				if(lPort < 1024 || lPort > 65535) throw new Exception();
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this, TOKEN_INVALID_PORT, getTitle(), JOptionPane.ERROR_MESSAGE);
				mTFPort.requestFocusInWindow();
				mTFPort.selectAll();
				return;
			}
			String lName = mTFName.getText();
			if(lName.equals(""))
			{
				JOptionPane.showMessageDialog(this, TOKEN_NO_NAME, getTitle(), JOptionPane.ERROR_MESSAGE);
				mTFName.requestFocusInWindow();
			}
			if(createServer(lPort))
			{ //We successfully created the server, now create the client for this user
				createClient(lName, "localhost", lPort, true); //This user created the game so is an admin
				this.dispose(); //We have the server and client set up so we're all done here
			}
		}
		else if (a.getActionCommand().equals(ACTION_JOIN))
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
			String lName = mTFName.getText();
			if(lName.equals(""))
			{
				JOptionPane.showMessageDialog(this, TOKEN_NO_NAME, getTitle(), JOptionPane.ERROR_MESSAGE);
				mTFName.requestFocusInWindow();
			}
			
			if(createClient(lName, mTFIPAddr.getText(), lPort, false))
			{ //The client was successfully created, we can dispose of this window now
				//this.dispose();
				this.setEnabled(false);
			}
		}
		
	}
}
