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
}
