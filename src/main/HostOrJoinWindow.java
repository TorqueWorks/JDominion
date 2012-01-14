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
	
	private static final String TOKEN_TITLE = "JDominion";
	private static final String TOKEN_INVALID_PORT = "The port was invalid. \nValid ports are between 1024 and 65535.";
	
	private static final String ACTION_JOIN = "JOIN";
	private static final String ACTION_HOST = "HOST";
	
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
		this.setTitle(TOKEN_TITLE);
		JPanel content = new JPanel(new BorderLayout());
		
		mBHost.setActionCommand(ACTION_HOST);
		mBHost.addActionListener(this);
		mBJoin.setActionCommand(ACTION_JOIN);
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
		mTFIPAddr.requestFocusInWindow();
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
			if(createServer(lPort))
			{ //We successfully created the server, now create the client for this user
				createClient("localhost", lPort);
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
			if(createClient(mTFIPAddr.getText(), lPort))
			{ //The client was successfully created, we can dispose of this window now
				this.dispose();
			}
		}
		
	}
}
