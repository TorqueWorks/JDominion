package dominion.client;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.game.DominionException;
import dominion.game.DominionPlayer;
import dominion.server.DominionServerProtocol;

import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class DominionClientProtocol implements SocketCallback{

	private DominionClient mClient;
	
	public static final String CLIENT_MSG_DELIM = "^";
	public static final String CLIENT_MSG_DELIM_REGEX = Pattern.quote(CLIENT_MSG_DELIM);
	
	public static final String JOIN_GAME_MSG = "JOINGAME";
	public static final int JOIN_GAME_MSG_NUM_FIELDS = 3;
	
	public static final int JOIN_GAME_MSG_LEN = 2;
	
	private static final String TOKEN_JOIN_GAME_SUCCESS = "Successfully joined game!";
	private static final String TOKEN_JOIN_GAME_FAILURE = "Failed to join game.";
	
	private Logger mLog = Logger.getLogger(DominionClientProtocol.class.getName());
	
	/**
	 * Sets the client that this protocol acts again.
	 * 
	 * @param aClient The client this protocol belongs to.
	 */
	public void setClient(DominionClient aClient)
	{
		mClient = aClient;
	}
	
	@Override
	public void processMessage(String aMessage, TorqueClientSocket aReceiver) {
		mLog.debug("Process Message - " + aMessage);
		String[] lTokens = aMessage.split(DominionServerProtocol.SERVER_MSG_DELIM_REGEX);
		
		if(lTokens.length < 1)
		{
			mLog.error("Received message with invalid number of fields - " + aMessage);
			return;
		}
		if(lTokens[0].equals(DominionServerProtocol.NEW_PLAYER_MSG))
		{
			processNewPlayerMessage(lTokens, aReceiver);
		}
		else if (lTokens[0].equals(DominionServerProtocol.JOIN_GAME_RESP_MSG))
		{
			processJoinGameRespMessage(lTokens, aReceiver);
		}
	}

	/**
	 * Creates a JOIN GAME message which is used to indicate to the server that a player wishes to join their game.
	 * 
	 * @param aName The name to display this user as
	 * @return The complete JOIN GAME message as a string
	 */
	public static String createJoinGameMessage(String aName)
	{
		String lMessage = JOIN_GAME_MSG;
		lMessage += ("^" + aName);
		return lMessage;
	}
	
	/**
	 * Process an incoming new player message. This message indicates that a new player has joined the game.
	 * 
	 * @param aTokens The parsed tokens of the message body
	 */
	private void processNewPlayerMessage(String[] aTokens, TorqueClientSocket aReceiver)
	{
		mLog.debug("Process New Player Message");
		if (aTokens.length == DominionServerProtocol.NEW_PLAYER_MSG_NUM_FIELDS)
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
	
	/**
	 * Process a received Join Game Response message. This message indicates the success of a join game request sent out by this client.
	 * 
	 * @param aTokens The tokens from the body of this message
	 * @param aReceiver The socket we received the message on
	 */
	private void processJoinGameRespMessage(String[] aTokens, TorqueClientSocket aReceiver)
	{
		mLog.debug("Process Join Game Resp Message");
		if(aTokens.length == DominionServerProtocol.JOIN_GAME_RESP_NUM_FIELDS)
		{
			boolean lSuccess = Boolean.parseBoolean(aTokens[1]);
			if(lSuccess)
			{
				mClient.displayMessage(TOKEN_JOIN_GAME_SUCCESS);
			}
			else
			{
				mClient.displayMessage(TOKEN_JOIN_GAME_FAILURE);
				try {
					//TODO: The server should be the one doing the disconnecting
					aReceiver.closeSocket();
				} catch (IOException e) {
					mLog.error("Could not close client socket - " + e.getMessage());
					return;
				}
			}
		}
	}
}
