package dominion.server;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.client.DominionClientProtocol;
import dominion.game.DominionException;
import dominion.game.DominionPlayer;
import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class DominionServerProtocol implements SocketCallback{

	private DominionServer mServer;
	
	public static final String SERVER_MSG_DELIM = "^";
	public static final String SERVER_MSG_DELIM_REGEX = Pattern.quote(SERVER_MSG_DELIM);
	
	public static final String NEW_PLAYER_MSG = "NEWPLAYER";
	public static final String JOIN_GAME_RESP_MSG = "JOINGAMERESP";
	
	public static final int NEW_PLAYER_MSG_NUM_FIELDS = 3;
	public static final int JOIN_GAME_RESP_NUM_FIELDS = 2;
	
	private static final String TOKEN_INVALID_MSG = "Invalid message format";
	private Logger mLog = Logger.getLogger(DominionServerProtocol.class.getName());
	

	/**
	 * Sets the server this Protocol will act against.
	 * 
	 * @param aServer The server this protocol belongs to
	 */
	public void setServer(DominionServer aServer)
	{
		mServer = aServer;
	}
	
	@Override
	public void processMessage(String aMessage, TorqueClientSocket aReceiver) {
		mLog.debug("Process message - " + aMessage);
		//Use pattern here because split expects a regex so if the delimiter is a reserved character we need to escape it
		String[] lTokens = aMessage.split(DominionClientProtocol.CLIENT_MSG_DELIM_REGEX);
		if(lTokens[0].equals(DominionClientProtocol.JOIN_GAME_MSG))
		{
			processJoinGameMessage(lTokens, aReceiver);
		}
		
	}

	/**
	 * Creates a new player message. This message indicates that a new player has joined the game.
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
	
	/**
	 * Creates a Join Game Response message. This indicates whether the join game request succeeded or not.
	 * This message has three fields:
	 * <ol>
	 * 		<li>MessageID - The JOIN_GAME_RESP_MSG identifier</li>
	 * 		<li>Success - 1 if the request was successful, 0 if it was not</li>
	 * </ol>
	 * @param aSuccessful <code>TRUE</code> if the join game request was successful, <code>FALSE</code> if it was not.
	 * @return
	 */
	public static String createJoinGameRespMessage(boolean aSuccessful, String aMessage)
	{
		StringBuilder lMessage = new StringBuilder(JOIN_GAME_RESP_MSG);
		lMessage.append(SERVER_MSG_DELIM);
		lMessage.append(aSuccessful);
		return lMessage.toString();
	}
	
	/**
	 * Process a received JOIN GAME message. This message indicates that a player wishes to join the game hosted
	 * by this server. 
	 * 
	 * @param aTokens The contents of the message body
	 * @param aReceiver The socket the message was received from
	 */
	private void processJoinGameMessage(String[] aTokens, TorqueClientSocket aReceiver)
	{
		mLog.debug("Process Join Game Message");
		boolean lSuccess = false;
		String lMessage = "";
		
		if(aTokens.length == DominionClientProtocol.JOIN_GAME_MSG_LEN)
		{ //Only process the message if it has the right number of tokens...
			try {
				mServer.addPlayer(aTokens[1]);
				lSuccess = true;
			} catch (DominionException e) {
				lMessage = e.getMessage();
			}
		}
		else
		{
			lMessage = TOKEN_INVALID_MSG;
		}
		try 
		{ //Send out response message indicating success of request
			aReceiver.sendMessage(createJoinGameRespMessage(lSuccess, lMessage));
		} catch (IOException e) {
			mLog.error(e.getMessage());
			return;
		}
	}
}
