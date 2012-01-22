package dominion.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import dominion.game.DominionGame.CardStack;

import torque.graphics.ImageLibrary;

public class DominionClientWindow extends JFrame implements ActionListener{

	private DominionClient mClient;
	private JPanel mContent, mAdminButtonPanel;
	private JButton mStartGame;
	private List<CardChoicePanel> mCardChoicePanels = new ArrayList<CardChoicePanel>();
	
	private JTextArea mTextArea;
	//Localization tokens
	public static final String TOKEN_START_GAME_BUTTON_TEXT = "Start Game";
	public static final String TOKEN_STARTING_BUTTON_TEXT = "Starting...";
	public static final String TOKEN_START_GAME_FAILED = "Failed to start game";
	public static final String TOKEN_JOINING_GAME = "Joining game...";

	private static final int CARD_COLUMNS = 5;
	private static final int CARD_ROWS = 2;
	
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
		loadImages();
		initComponents();
		this.setTitle("Dominion Client");
		this.setPreferredSize(new Dimension(1000, 750));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	/**
	 * Starts a thread to load the image files then returns. Note this means that the images will not immediately
	 * be available upon return from this method.
	 */
	private void loadImages()
	{
		ImageLoader lLoader = new ImageLoader();
		lLoader.start();
		try {
			//TODO: Temp, let the images load...
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates and initializes the various components of the GUI.
	 */
	private void initComponents()
	{
		mContent = new JPanel(new GridBagLayout());
		JScrollPane lContentScrollPane = new JScrollPane(mContent);

		lContentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lContentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_START; //Anchors it to top of page
		c.fill = GridBagConstraints.NONE; //Don't fill out the components
		c.insets = new Insets(5,5,5,5); //Give each component 5px buffer on each side
		c.weightx = 1.0; //Causes the extra x space to be distributed evenly among all components
		
		for(int lRow = 0; lRow < CARD_ROWS; lRow++)
		{
			c.gridy = lRow;
			for(int lColumn = 0; lColumn < CARD_COLUMNS; lColumn++)
			{
				c.gridx = lColumn;
				CardChoicePanel lCCP = new CardChoicePanel((lRow * CARD_ROWS) + lColumn, mClient);
				mContent.add(lCCP,c);
				mCardChoicePanels.add(lCCP);
			}
		}
		
		c.fill = GridBagConstraints.BOTH;
		c.gridy = GridBagConstraints.RELATIVE; //Each component is put at grid y of previous component + 1
		c.gridx = 0; //Start each component on a new row(each component has a grid x value of 0)
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0,0,0,0);
	
		c.weighty = 1.0;
		c.weightx = 1.0;
		mTextArea = new JTextArea();
		//mTextArea.setPreferredSize(new Dimension(200,200));
		mContent.add(mTextArea,c);
		
		if(mClient.isAdmin())
		{
			JPanel mAdminButtonPanel = new JPanel();
			
			mStartGame = new JButton(TOKEN_START_GAME_BUTTON_TEXT);
			mStartGame.setActionCommand(ACTION_START_GAME);
			mStartGame.addActionListener(this);
			mAdminButtonPanel.add(mStartGame);
			
			c.anchor = GridBagConstraints.PAGE_END;
			c.fill = GridBagConstraints.NONE;
			c.weighty = 0.0;
			mContent.add(mAdminButtonPanel,c);
		}
		this.setContentPane(lContentScrollPane);

		
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
				mClient.sendMessage(DominionClientProtocol.createStartGameMessage(mClient.getID()));
			} catch (IOException e) {
				mLog.error("Failed to start game - " + e.getMessage());
				displayMessage(TOKEN_START_GAME_FAILED);
			}
			this.setEnabled(false);
			mStartGame.setEnabled(false);
			mStartGame.setText(TOKEN_STARTING_BUTTON_TEXT);
		}
	}
	
	public void refreshCardPool()
	{
		CardStack[] lCardPool = mClient.getCardsInPool();
		for(int i = 0; i < lCardPool.length && i < mCardChoicePanels.size(); ++i)
		{
			CardStack lCS = lCardPool[i];
			if(lCS == null) continue;
			mCardChoicePanels.get(i).setSelectedValue(lCS.getCard());
		}
	}
	/**
	 * Simple thread class to load all the images for the client UI
	 * @author gagnoncl
	 *
	 */
	private class ImageLoader extends Thread
	{
		
		@Override
		public void run()
		{
			ImageLibrary.loadImage("adventurer", "images/Adventurer.bmp");
			ImageLibrary.loadImage("bureaucrat", "images/Bureaucrat.bmp");
			ImageLibrary.loadImage("cellar", "images/Cellar.bmp");
			ImageLibrary.loadImage("chancellor", "images/Chancellor.bmp");
			ImageLibrary.loadImage("chapel", "images/Chapel.bmp");
			ImageLibrary.loadImage("council_room", "images/Council_Room.bmp");
			ImageLibrary.loadImage("feast", "images/Feast.bmp");
			ImageLibrary.loadImage("festival", "images/Festival.bmp");
			ImageLibrary.loadImage("gardens", "images/Gardens.bmp");
			ImageLibrary.loadImage("laboratory", "images/Laboratory.bmp");
			ImageLibrary.loadImage("library", "images/Library.bmp");
			ImageLibrary.loadImage("market", "images/Market.bmp");
			ImageLibrary.loadImage("militia", "images/Militia.bmp");
			ImageLibrary.loadImage("mine", "images/Mine.bmp");
			ImageLibrary.loadImage("moat", "images/Moat.bmp");
			ImageLibrary.loadImage("moneylender", "images/Moneylender.bmp");
			ImageLibrary.loadImage("remodel", "images/Remodel.bmp");
			ImageLibrary.loadImage("smithy", "images/Smithy.bmp");
			ImageLibrary.loadImage("spy","images/Spy.bmp");
			ImageLibrary.loadImage("thief", "images/Thief.bmp");
			ImageLibrary.loadImage("throne_room", "images/Throne_Room.bmp");
			ImageLibrary.loadImage("village", "images/Village.bmp");
			ImageLibrary.loadImage("witch", "images/Witch.bmp");
			ImageLibrary.loadImage("woodcutter","images/Woodcutter.bmp");
			ImageLibrary.loadImage("workshop", "images/Workshop.bmp");
		}
	}
}
