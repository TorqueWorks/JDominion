package dominion.client;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DominionClientWindow extends JFrame{

	private DominionClient mClient;
	private JPanel mContent;
	public JTextArea mTextArea;
	
	public DominionClientWindow(DominionClient aClient)
	{
		mClient = aClient;
		init();
	}
	
	private void init()
	{
		mContent = new JPanel();
		
		mTextArea = new JTextArea();
		mTextArea.setPreferredSize(new Dimension(200,200));
		mContent.add(mTextArea);
		
		this.setContentPane(mContent);
		this.setTitle("Dominion Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		
	}
}
