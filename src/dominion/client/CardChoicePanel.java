package dominion.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import torque.graphics.ImageLibrary;

import dominion.game.Card;
import dominion.game.Cards;

public class CardChoicePanel extends JPanel implements ActionListener{

	private JComboBox mChoices;
	private JLabel mImage;
	
	private final DominionClient mClient;
	private final int mID;
	
	private static final int IMAGE_HEIGHT = 225;
	private static final int IMAGE_WIDTH = 150;
	
	public static final String TOKEN_APPLY = "Apply";
	
	private static final String ACTION_COMMAND_APPLY = "APPLY";
	private static final String ACTION_COMMAND_CHOICES = "CARDCHANGE";
	private static final Logger mLog = Logger.getLogger(CardChoicePanel.class.getName());
	public CardChoicePanel(int aID, DominionClient aClient)
	{
		super(new GridBagLayout());
		mClient = aClient;
		mID = aID;
		
		setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT + 50));
		setBorder(BorderFactory.createLineBorder(Color.black));
		initComponents();
	}
	
	/**
	 * Initializes the GUI components
	 */
	private void initComponents()
	{	
		GridBagConstraints lPanelConstraints = new GridBagConstraints();
		lPanelConstraints.gridx = 0;
		lPanelConstraints.gridy = 0;
		lPanelConstraints.gridwidth = 2;
		lPanelConstraints.anchor = GridBagConstraints.PAGE_START;
		lPanelConstraints.fill = GridBagConstraints.BOTH;
		
		mImage = new JLabel();
		this.add(mImage,lPanelConstraints);
		
		mChoices = new JComboBox();
		mChoices.setActionCommand(ACTION_COMMAND_CHOICES);
		mChoices.addActionListener(this);
		//TODO: Don't hardcode this
		for(int i = 6; i < Cards.mCards.size(); i++)
		{
			mChoices.addItem(Cards.mCards.get(i));
		}
		
		GridBagConstraints lChoicesConstraints = new GridBagConstraints();
		lChoicesConstraints.weighty= 1.0;
		lChoicesConstraints.fill = GridBagConstraints.NONE;
		lChoicesConstraints.anchor = GridBagConstraints.PAGE_END;
		lChoicesConstraints.gridx = 0;
		lChoicesConstraints.gridy = 1;
		
		this.add(mChoices, lChoicesConstraints);
		
		GridBagConstraints lApplyButtonConstraints = new GridBagConstraints();
		lApplyButtonConstraints.weighty= 1.0;
		lApplyButtonConstraints.fill = GridBagConstraints.NONE;
		lApplyButtonConstraints.anchor = GridBagConstraints.PAGE_END;
		lApplyButtonConstraints.gridx = 1;
		lApplyButtonConstraints.gridy = 1;
		
		JButton lApply = new JButton(TOKEN_APPLY);
		lApply.setActionCommand(ACTION_COMMAND_APPLY);
		lApply.addActionListener(this);
		this.add(lApply, lApplyButtonConstraints);
	}

	protected void setSelectedValue(Card aCard)
	{
		mLog.debug("Setting selected value of Panel " + mID + " to " + aCard);
		mChoices.setSelectedItem(aCard);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(ACTION_COMMAND_CHOICES))
		{ //New card was selected from dropdown, update the image and send a message
			Card lSelected = ((Card)mChoices.getSelectedItem());
			String lName = lSelected.toString().toLowerCase().replace(' ', '_');
			Image lImage = ImageLibrary.getScaledImage(lName, new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
			mImage.setIcon(new ImageIcon(lImage));
		}
		else if (e.getActionCommand().equals(ACTION_COMMAND_APPLY))
		{
			Card lSelected = ((Card)mChoices.getSelectedItem());
			mClient.sendMessageGuaranteed(DominionClientProtocol.createChooseCardMessage(mID, lSelected.getID()));
		}
	}
}
