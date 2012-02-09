package dominion.client;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.game.Card;
import dominion.game.Cards;
import dominion.game.DominionException;
import dominion.server.DominionServerProtocol;

import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class DominionClientProtocol implements SocketCallback{

	private DominionClient mClient;
	
	public static final String CLIENT_MSG_DELIM = "^";
	public static final String CLIENT_MSG_DELIM_REGEX = Pattern.quote(CLIENT_MSG_DELIM);
	
	//Message IDs
	public static final String JOIN_GAME_MSG = "JOINGAME";
	public static final String START_GAME_MSG = "STARTGAME";
	public static final String CHOOSE_CARD_MSG = "CHOOSECARD";
	public static final String REQUEST_POOL_LIST_MSG = "REQUESTPOOLLIST";
	
	//Message field count
	public static final int JOIN_GAME_MSG_NUM_FIELDS = 3;
	public static final int START_GAME_MSG_NUM_FIELDS = 1;
	public static final int CHOOSE_CARD_MSG_NUM_FIELDS = 3;
	public static final int REQUEST_POOL_LIST_MSG_NUM_FIELDS = 1;
	
	//Localization tokens
	private static final String TOKEN_JOIN_GAME_SUCCESS = "Successfully joined game!";
	private static final String TOKEN_JOIN_GAME_FAILURE = "Failed to join game.";
	private static final String TOKEN_GAME_START = "Let the games begin!";
	
	private Logger mLog = Logger.getLogger(DominionClientProtocol.class.getName());
	
	/**
	 * Sets the client that this protocol acts again.
	 * 
	 * @param aClient The client this protocol belongs to.
	 */
	protected void setClient(DominionClient aClient)
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
		try
		{
			if(lTokens[0].equals(DominionServerProtocol.NEW_PLAYER_MSG))
			{
				processNewPlayerMessage(lTokens, aReceiver);
			}
			else if (lTokens[0].equals(DominionServerProtocol.JOIN_GAME_RESP_MSG))
			{
				processJoinGameRespMessage(lTokens, aReceiver);
			}
			else if(lTokens[0].equals(DominionServerProtocol.START_GAME_MSG))
			{
				processStartGameMessage(lTokens);
			}
			else if (lTokens[0].equals(DominionServerProtocol.CARD_CHOSEN_MSG))
			{
				processCardChosenMessage(lTokens);
			}
			else if (lTokens[0].equals(DominionServerProtocol.POOL_LIST_MSG))
			{
				processPoolListMessage(lTokens);
			}
			else
			{
				mLog.debug("Unknown message received - " + aMessage);
			}
		}
		catch(Exception e)
		{
			mLog.error("Error processing message (" + aMessage + ") - " + e.getMessage());
		}
	}

	/**
	 * Creates a JOIN GAME message which is used to indicate to the server that a player wishes to join their game.
	 * This message has three fields:
	 * <ol>
	 * 		<li>MessageID - The START_GAME_MSG identifier</li>
	 * 		<li>Name - The name the player wishes to display themselves as</li>
	 * 		<li>IsAdmin - Whether the player is an admin for the game or not </li>
	 * </ol>
	 * @param aName The name to display this user as
	 * @return The complete JOIN GAME message as a string
	 */
	public static String createJoinGameMessage(String aName, boolean aIsAdmin)
	{
		StringBuilder lMessage = new StringBuilder(JOIN_GAME_MSG);
		lMessage.append(CLIENT_MSG_DELIM);
		lMessage.append(aName);
		lMessage.append(CLIENT_MSG_DELIM);
		lMessage.append(aIsAdmin);
		return lMessage.toString();
	}
	
	/**
	 * Creates a START GAME message which is used to tell the server to start the game.
	 * This message has two fields:
	 * <ol>
	 * 		<li>MessageID - The JOIN_GAME_RESP_MSG identifier</li>
	 * 		<li>PlayerID - The ID of this player</li>
	 * </ol>
	 * @return The complete START GAME message as a string
	 */
	public static String createStartGameMessage(int aPlayerID)
	{
		StringBuilder lMessage = new StringBuilder(START_GAME_MSG);
		return lMessage.toString();
	}
	
	/**
	 * Creates a CHOOSE CARD message which is used to tell the server that a client has
	 * chosen a card to use in the pool.
	 * <ol>
	 * 		<li>Message ID - The CHOOSE_CARD_MSG identifier</li>
	 * 		<li>Index - The slot index for this card</li>
	 * 		<li>CardID - The ID of the card being chosen</li>
	 * </ol>
	 * 
	 * @param aIndex Slot index in the card pool
	 * @param aCardID ID of the card being chosen
	 * @return The complete CHOOSE CARD message as a string
	 */
	public static String createChooseCardMessage(int aIndex, int aCardID)
	{
		StringBuilder lMessage = new StringBuilder(CHOOSE_CARD_MSG);
		lMessage.append(CLIENT_MSG_DELIM);
		lMessage.append(aIndex);
		lMessage.append(CLIENT_MSG_DELIM);
		lMessage.append(aCardID);
		return lMessage.toString();
	}
	
	/**
	 * Creates a REQUESTPOOLLIST message which is used to request the server send an updated version of the pool
	 * list to this client.
	 * 
	 * @return The complete REQUESTPOOLLIST message as a string
	 */
	public static String createRequestPoolListMessage()
	{
		StringBuilder lMessage = new StringBuilder(REQUEST_POOL_LIST_MSG);
		return lMessage.toString();
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
	 * Process a received Join Game Response message. This message gives the client their PlayerID, or a -1 if the
	 * request failed.
	 * 
	 * @param aTokens The tokens from the body of this message
	 * @param aReceiver The socket we received the message on
	 */
	private void processJoinGameRespMessage(String[] aTokens, TorqueClientSocket aReceiver)
	{
		mLog.debug("Process Join Game Resp Message");
		if(aTokens.length == DominionServerProtocol.JOIN_GAME_RESP_NUM_FIELDS)
		{
			int lPlayerID = Integer.parseInt(aTokens[1]);
			if(lPlayerID >= 0) //A -1 is returned if there was an error
			{
				mClient.setID(lPlayerID);
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
	
	/**
	 * Process a received init game message. This message tells the client that the game is starting and gives them the initial
	 * pool of cards (along with how many of each card there is).
	 * 
	 * @param aTokens The tokens from the body of the message
	 */
	private void processStartGameMessage(String[] aTokens)
	{
		mLog.debug("Process Init Game Message");
		if(aTokens.length == DominionServerProtocol.START_GAME_NUM_FIELDS)
		{
			mClient.displayMessage(TOKEN_GAME_START);
		}
	}
	
	/**
	 * Process a received CARD CHOSEN message. This message tells the client that a new card has been chosen for the
	 * pool during the setup phase.
	 * 
	 * @param aTokens The tokens from the body of the message
	 * @throws DominionException 
	 * @throws NumberFormatException 
	 */
	private void processCardChosenMessage(String[] aTokens) throws NumberFormatException, DominionException
	{
		mLog.debug("Process Card Chosen message");
		if(aTokens.length == DominionServerProtocol.CARD_CHOSEN_NUM_FIELDS)
		{
			int lIndex = Integer.parseInt(aTokens[1]);
			Card lCard = null;
			try {
				lCard = Cards.getCardByID(Integer.parseInt(aTokens[2]));
			}
			catch(DominionException ignore) {}
			
			mClient.addCardToPool(lIndex, lCard, 10); //TODO: Don't hardcode number here
		}
	}
	
	/**
	 * Processes a received POOL LIST message. This message contains a list of all the cards in the pool for this game
	 * and how many of each card are left
	 * 
	 * @param aTokens The tokens from the body of the message
	 */
	private void processPoolListMessage(String aTokens[])
	{
		String[] lPoolTokens = aTokens[1].split(DominionServerProtocol.SERVER_MSG_SUBFIELD_DELIM_REGEX);
		for(int i = 0; i < lPoolTokens.length - 1; i+=2) //The -1 from the length is so that we ignore the last item if there's an odd number of values
		{
			int lCardID = Integer.parseInt(lPoolTokens[i]);
			int lNum = Integer.parseInt(lPoolTokens[i + 1]);
			Card lCard = null;
			try {
				lCard = Cards.getCardByID(lCardID);
			} catch (DominionException ignore) {} //invalid card ids still need to be set to null in the card list
			try {
				mClient.addCardToPool(i / 2, lCard, lNum);
			} catch (DominionException e) { //Just log and move on..not much else we can do
				mLog.debug("Error adding card to pool from message - " + e.getMessage());
				continue;
			}
		}
	}
}
