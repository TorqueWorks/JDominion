package dominion.server;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.client.DominionClientProtocol;
import dominion.game.Card;
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
	public static final String INIT_GAME_MSG = "INITGAME";
	
	public static final int NEW_PLAYER_MSG_NUM_FIELDS = 3;
	public static final int JOIN_GAME_RESP_NUM_FIELDS = 2;
	public static final int INIT_GAME_NUM_FIELDS = 2;
	
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
		
		if(lTokens.length < 1)
		{
			mLog.debug("Received message with invalid number of fields - " + aMessage);
			return;
		}
		
		try
		{
			if(lTokens[0].equals(DominionClientProtocol.JOIN_GAME_MSG))
			{
				processJoinGameMessage(lTokens, aReceiver);
			}
			else if(lTokens[0].equals(DominionClientProtocol.START_GAME_MSG))
			{
				processStartGameMessage(lTokens);
			}
			else
			{
				mLog.debug("Received unknown message - " + aMessage);
			}
		}
		catch(Exception e)
		{ //Catch everything here so we can keep processing even if an exception is thrown
			mLog.error("Exception while processing message (" + aMessage + ") - " + e.getMessage());
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
	 * Creates an init game message. This message indicates to a player that the game is starting. It
	 * also includes the starting pool of cards.
	 * This message has two fields:
	 * <ol>
	 * 		<li>MessageID - The INIT_GAME_MSG identifer</li>
	 * 		<li>StartingCards - The starting cards for this game</li>
	 * </ol>
	 * @param aCardPool
	 * @return The formatted message as a string
	 */
	public static String createInitGameMessage(Map<Card,Integer> aCardPool)
	{
		StringBuilder lMessage = new StringBuilder(INIT_GAME_MSG);
		lMessage.append(SERVER_MSG_DELIM);
		for(Card c : aCardPool.keySet())
		{
			lMessage.append(c.getID());
			lMessage.append("~");
			lMessage.append(aCardPool.get(c));
			lMessage.append("~");
		}
		return lMessage.toString();
	}
	/**
	 * Creates a Join Game Response message. This indicates whether the join game request succeeded or not.
	 * This message has two fields:
	 * <ol>
	 * 		<li>MessageID - The JOIN_GAME_RESP_MSG identifier</li>
	 * 		<li>PlayerID - The ID to be assigned to the player, -1 if the player couldn't be added</li>
	 * </ol>
	 * @param aSuccessful <code>TRUE</code> if the join game request was successful, <code>FALSE</code> if it was not.
	 * @return
	 */
	public static String createJoinGameRespMessage(int aPlayerID)
	{
		StringBuilder lMessage = new StringBuilder(JOIN_GAME_RESP_MSG);
		lMessage.append(SERVER_MSG_DELIM);
		lMessage.append(aPlayerID);
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
		int lPlayerID = -1;
		if(aTokens.length == DominionClientProtocol.JOIN_GAME_MSG_NUM_FIELDS)
		{ //Only process the message if it has the right number of tokens...
			boolean lIsAdmin = Boolean.parseBoolean(aTokens[2]);
			try {
				DominionPlayer lPlayer = mServer.addPlayer(aTokens[1], lIsAdmin);
				lPlayerID = lPlayer.getID();
			} catch (DominionException e) {
				//Leave success as false...
			}
		}
		try 
		{ //Send out response message indicating success of request
			aReceiver.sendMessage(createJoinGameRespMessage(lPlayerID));
		} catch (IOException e) {
			mLog.error(e.getMessage());
			return;
		}
	}
	
	/**
	 * Processes a received START GAME message. This message indicates that a player wishes to start
	 * the game. 
	 * 
	 * @param aTokens The contents of the message body
	 */
	private void processStartGameMessage(String[] aTokens)
	{
		mLog.debug("Processing Start Game message");
		if(aTokens.length == DominionClientProtocol.START_GAME_MSG_NUM_FIELDS)
		{
			int lPlayerID = Integer.parseInt(aTokens[1]);
			if(mServer.isPlayerAdmin(lPlayerID))
			{ //Make sure the player is an admin before starting the game...
				try {
					mServer.startGame();
				} catch (IOException e) {
					mLog.error(e.getMessage());
				}
			}
		}
	}
}
