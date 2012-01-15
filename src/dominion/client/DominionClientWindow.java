package dominion.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

public class DominionClientWindow extends JFrame implements ActionListener{

	private DominionClient mClient;
	private JPanel mContent, mAdminButtonPanel;
	private JButton mStartGame;
	
	private JTextArea mTextArea;
	//Localization tokens
	private static final String TOKEN_START_GAME_BUTTON_TEXT = "Start Game";
	private static final String TOKEN_STARTING_BUTTON_TEXT = "Starting...";
	private static final String TOKEN_START_GAME_FAILED = "Failed to start game";
	//Actions
	private static final String ACTION_START_GAME = "START";
	
	private static final Logger mLog = Logger.getLogger(DominionClientWindow.class.getName());
	
	/**
	 * The UI for a client. 
	 * @param aClient
	 */
	public DominionClientWindow(DominionClient aClient)
	{
		mClient = aClient;
		initComponents();
		this.setTitle("Dominion Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	/**
	 * Creates and initializes the various components of the GUI.
	 */
	private void initComponents()
	{
		mContent = new JPanel(new BorderLayout());
		
		mTextArea = new JTextArea();
		mTextArea.setPreferredSize(new Dimension(200,200));
		mContent.add(mTextArea, BorderLayout.CENTER);
		
		if(mClient.isAdmin())
		{
			JPanel mAdminButtonPanel = new JPanel();
			
			mStartGame = new JButton(TOKEN_START_GAME_BUTTON_TEXT);
			mStartGame.setActionCommand(ACTION_START_GAME);
			mStartGame.addActionListener(this);
			
			mAdminButtonPanel.add(mStartGame);
			mContent.add(mAdminButtonPanel, BorderLayout.SOUTH);
		}
		
		this.setContentPane(mContent);

		
	}
	
	/**
	 * Prints a message to the UI. Will ignore empty strings. Will also add a newline after every message so including
	 * newlines in the message itself will cause the dialog to skip multiple lines.
	 * 
	 * @param aMessage The message to display
	 */
	protected void displayMessage(String aMessage)
	{
		if(!aMessage.equals("")) 
		{
			mTextArea.append(aMessage);
			mTextArea.append("\n");
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(ACTION_START_GAME))
		{
			try {
				mClient.sendMessage(DominionClientProtocol.createStartGameMessage(mClient.getPlayerID()));
			} catch (IOException e) {
				mLog.error("Failed to start game - " + e.getMessage());
				displayMessage(TOKEN_START_GAME_FAILED);
			}
			mStartGame.setEnabled(false);
			mStartGame.setText(TOKEN_STARTING_BUTTON_TEXT);
		}
	}
}
