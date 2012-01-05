package dominion.game;

import org.apache.log4j.Logger;

public class DominionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6590554238842612349L;
	private static final Logger mLog = Logger.getLogger(DominionException.class.getName());
	
	public DominionException(String aMethod, String aMessage)
	{
		super(aMessage);
		mLog.error(aMethod + " - " + aMessage);
	}
}
