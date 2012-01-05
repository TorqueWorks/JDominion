package dominion.server;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.client.DominionClientProtocol;
import dominion.game.DominionPlayer;
import torque.sockets.SocketCallback;

public class DominionServerProtocol implements SocketCallback{

	private DominionServer mServer;
	
	public static final String SERVER_MSG_DELIM = "^";
	public static final String SERVER_MSG_DELIM_REGEX = Pattern.quote(SERVER_MSG_DELIM);
	public static final String NEW_PLAYER_MSG = "NEWPLAYER";
	public static final int NEW_PLAYER_MSG_LEN = 3;
	
	private Logger mLog = Logger.getLogger(DominionServerProtocol.class.getName());
	
	public void setServer(DominionServer aServer)
	{
		mServer = aServer;
	}
	
	@Override
	public void processMessage(String aMessage) {
		mLog.debug("Process message - " + aMessage);
		//Use pattern here because split expects a regex so if the delimiter is a reserved character we need to escape it
		String[] lTokens = aMessage.split(DominionClientProtocol.CLIENT_MSG_DELIM_REGEX);
		if(lTokens[0].equals(DominionClientProtocol.JOIN_GAME_MSG))
		{
			processJoinGameMessage(lTokens);
		}
		
	}

	/**
	 * Creates a new player message. This message is in the format
	 * This message has three fields:
	 * <ol>
	 * 		<li>MessageID - The NEW_PLAYER_MSG identifier 
	 * 		<li>PlayerName - The string name of the player
	 * 		<li>PlayerID - The internal ID used to refer to the player
	 * </ol>
	 * @param aPlayer The player we're making a message for
	 * @return The formatted message as a string
	 */
	public static String createNewPlayerMessage(DominionPlayer aPlayer)
	{
		StringBuilder lMessage = new StringBuilder(NEW_PLAYER_MSG);
		lMessage.append(SERVER_MSG_DELIM);
		lMessage.append(aPlayer.getName());
		lMessage.append(SERVER_MSG_DELIM);
		lMessage.append(aPlayer.getID());
		return lMessage.toString();
	}
	
	private void processJoinGameMessage(String[] aTokens)
	{
		mLog.debug("Process Join Game Message");
		if(aTokens.length == DominionClientProtocol.JOIN_GAME_MSG_LEN)
		{ //Only process the message if it has the right number of tokens...
			mServer.addPlayer(aTokens[1]);
		}
	}
}
