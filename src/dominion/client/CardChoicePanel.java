package dominion.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import torque.graphics.ImageLibrary;

import dominion.game.Cards;

public class CardChoicePanel extends JPanel implements ActionListener{

	private JComboBox mChoices;
	private JPanel mContent;
	private JLabel mImage;
	
	private static final int IMAGE_HEIGHT = 225;
	private static final int IMAGE_WIDTH = 150;
	
	public CardChoicePanel()
	{
		super(new GridBagLayout());
		setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT + 50));
		setBorder(BorderFactory.createLineBorder(Color.black));
		initComponents();
	}
	
	private void initComponents()
	{	
		GridBagConstraints lPanelConstraints = new GridBagConstraints();
		lPanelConstraints.gridx = 0;
		lPanelConstraints.gridy = 0;
		lPanelConstraints.anchor = GridBagConstraints.PAGE_START;
		lPanelConstraints.fill = GridBagConstraints.BOTH;
		
		mImage = new JLabel();
		this.add(mImage,lPanelConstraints);
		
		mChoices = new JComboBox();
		mChoices.addActionListener(this);
		//TODO: Don't hardcode this
		for(int i = 6; i < Cards.mCards.size(); i++)
		{
			mChoices.addItem(Cards.mCards.get(i).getName());
		}
		
		GridBagConstraints lChoicesConstraints = new GridBagConstraints();
		lChoicesConstraints.weighty= 1.0;
		lChoicesConstraints.fill = GridBagConstraints.NONE;
		lChoicesConstraints.anchor = GridBagConstraints.PAGE_END;
		lChoicesConstraints.gridx = 0;
		lChoicesConstraints.gridy = 1;
		
		this.add(mChoices, lChoicesConstraints);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mChoices)
		{
			String lSelected = ((String)mChoices.getSelectedItem()).toLowerCase();
			//lSelected = lSelected.replace(' ', '_');
			Image lImage = ImageLibrary.getScaledImage(lSelected, new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
			mImage.setIcon(new ImageIcon(lImage));
		}
	}
}
