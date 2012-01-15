package dominion.client;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DominionClientWindow extends JFrame{

	private DominionClient mClient;
	private JPanel mContent;
	public JTextArea mTextArea;
	
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
		mContent = new JPanel();
		
		mTextArea = new JTextArea();
		mTextArea.setPreferredSize(new Dimension(200,200));
		mContent.add(mTextArea);
		
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
}
