package dominion.client;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.game.DominionException;
import dominion.game.DominionPlayer;
import dominion.server.DominionServerProtocol;

import torque.sockets.SocketCallback;

public class DominionClientProtocol implements SocketCallback{

	private DominionClient mClient;
	
	public static final String CLIENT_MSG_DELIM = "^";
	public static final String CLIENT_MSG_DELIM_REGEX = Pattern.quote(CLIENT_MSG_DELIM);
	
	public static final String JOIN_GAME_MSG = "JOINGAME";
	
	public static final int JOIN_GAME_MSG_LEN = 2;
	
	private Logger mLog = Logger.getLogger(DominionClientProtocol.class.getName());
	
	public DominionClientProtocol()
	{
	}
	
	public void setClient(DominionClient aClient)
	{
		mClient = aClient;
	}
	public void processMessage(String aMessage) {
		mLog.debug("Process Message - " + aMessage);
		String[] lTokens = aMessage.split(DominionServerProtocol.SERVER_MSG_DELIM_REGEX);
		
		if(lTokens[0].equals(DominionServerProtocol.NEW_PLAYER_MSG))
		{
			processNewPlayerMessage(lTokens);
		}
	}

	public static String createJoinGameMessage(String aName)
	{
		String lMessage = JOIN_GAME_MSG;
		lMessage += ("^" + aName);
		return lMessage;
	}
	
	private void processNewPlayerMessage(String[] aTokens)
	{
		mLog.debug("Process New Player Message");
		if (aTokens.length == 3)
		{
			try
			{
				mClient.addPlayer(aTokens[1], Integer.parseInt(aTokens[2]));
			}
			catch(NumberFormatException e)
			{
				mLog.error("Invalid ID in New Player Message - " + aTokens[2]);
				return;
			}
			catch(DominionException de)
			{
				mLog.error(de.getMessage());
				return;
			}
		}
	}
}
