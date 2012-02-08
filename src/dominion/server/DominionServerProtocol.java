package dominion.server;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dominion.client.DominionClientProtocol;
import dominion.game.Card;
import dominion.game.Cards;
import dominion.game.DominionException;
import dominion.game.DominionPlayer;
import dominion.game.CardStack;
import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class DominionServerProtocol implements SocketCallback{

	private DominionServer mServer;
	
	
	public static final String SERVER_MSG_DELIM = "^";
	public static final String SERVER_MSG_DELIM_REGEX = Pattern.quote(SERVER_MSG_DELIM);
	public static final String SERVER_MSG_SUBFIELD_DELIM = "~";
	public static final String SERVER_MSG_SUBFIELD_DELIM_REGEX = Pattern.quote(SERVER_MSG_SUBFIELD_DELIM);
	
	//Message IDs
	public static final String NEW_PLAYER_MSG = "NEWPLAYER";
	public static final String JOIN_GAME_RESP_MSG = "JOINGAMERESP";
	public static final String START_GAME_MSG = "STARTGAME";
	public static final String CARD_CHOSEN_MSG = "CARDCHOSEN";
	public static final String POOL_LIST_MSG = "POOLLIST";
	
	//Message field counts
	public static final int NEW_PLAYER_MSG_NUM_FIELDS = 3;
	public static final int JOIN_GAME_RESP_NUM_FIELDS = 2;
	public static final int START_GAME_NUM_FIELDS = 2;
	public static final int CARD_CHOSEN_NUM_FIELDS = 3;
	public static final int POOL_LIST_NUM_FIELDS = 2;
		
	//Localization tokens
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
			else if (lTokens[0].equals(DominionClientProtocol.CHOOSE_CARD_MSG))
			{
				processChooseCardMessage(lTokens);
			}
			else if(lTokens[0].equals(DominionClientProtocol.REQUEST_POOL_LIST_MSG))
			{
				processRequestPoolListMessage(lTokens, aReceiver);
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
	 * Creates an start game message. This message indicates to a client that the game is starting.
	 * This message has one fields:
	 * <ol>
	 * 		<li>MessageID - The STARTGAME_MSG identifer</li>
	 * </ol>
	 * @param aCardPool
	 * @return The formatted message as a string
	 */
	public static String createStartGameMessage()
	{
		StringBuilder lMessage = new StringBuilder(START_GAME_MSG);
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
	 * Creates a CARD CHOSEN message. This indicates that a new card has been chosen for the pool.
	 * This message has three fields:
	 * <ol>
	 * 		<li>MessageID - The CARD_CHOSEN_MSG identifier</li>
	 * 		<li>Index - The slot in the pool this card will take</li>
	 * 		<li>CardID - The ID of the card to add to the pool</li>
	 * </ol>
	 * 
	 * @param aIndex The slot index in the pool for the chosen card
	 * @param aCardID The ID of the card
	 * @return The formatted message as a string
	 */
	public static String createCardChosenMessage(int aIndex, int aCardID)
	{
		StringBuilder lMessage = new StringBuilder(CARD_CHOSEN_MSG);
		lMessage.append(SERVER_MSG_DELIM);
		lMessage.append(aIndex);
		lMessage.append(SERVER_MSG_DELIM);
		lMessage.append(aCardID);
		return lMessage.toString();
	}
	
	/**
	 * Creates a POOL LIST message. This tells a client which cards are in the pool and how many of those
	 * cards are left. This message has two fields:
	 * <ol>
	 * 		<li>MessageID - The POOL_LIST_MSG identifier</li>
	 * 		<li>CardList - A subfield delimited list of each card followed by how many of it are in the pool</li>
	 * </ol>
	 * 
	 * @param aCards The cards currently in the pool
	 * @return The formatted message as a string
	 */
	public static String createPoolListMessage(CardStack[] aCards)
	{
		StringBuilder lMessage = new StringBuilder(POOL_LIST_MSG);
		for(CardStack lCS : aCards)
		{
			lMessage.append(SERVER_MSG_SUBFIELD_DELIM);
			lMessage.append(lCS.getCard().getID());
			lMessage.append(SERVER_MSG_SUBFIELD_DELIM);
			lMessage.append(lCS.getTotal());
		}
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
		{ 
			boolean lIsAdmin = Boolean.parseBoolean(aTokens[2]);
			try {
				DominionPlayer lPlayer = mServer.addPlayer(aTokens[1], lIsAdmin);
				lPlayerID = lPlayer.getID();
			} catch (DominionException e) {}
		}
		aReceiver.sendMessageGuaranteed(createJoinGameRespMessage(lPlayerID));
		if(lPlayerID >= 0)
		{ //Non-negative player ID means that the player joined the game successfully so send them a list of the cards in the pool
			aReceiver.sendMessageGuaranteed(createPoolListMessage(mServer.getCardsInPool()));
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
	/**
	 * Processes a received CHOOSE CARD message. This message indicates that a player has chosen a new card
	 * for the pool.
	 * 
	 * @param aTokens The contents of the message body
	 */
	private void processChooseCardMessage(String [] aTokens)
	{
		mLog.debug("Processing Choose Card Message");
		if(aTokens.length == DominionClientProtocol.CHOOSE_CARD_MSG_NUM_FIELDS)
		{
			int lCardIndex = Integer.parseInt(aTokens[1]);
			int lCardID = Integer.parseInt(aTokens[2]);
			try {
				mServer.addCardToPool(lCardIndex, Cards.getCardByID(lCardID), 10); //TODO: Temp value here for number of cards
			} catch (DominionException e) {
				//Do nothing, dominion exceptions log themselves
			}
		}
	}
	
	/**
	 * Processes a received REQUEST POOL LIST message. This message indicates a client wants the list of the cards
	 * in the pool.
	 * 
	 * @param aTokens The contents of the message body
	 * @param aReceiver The client which sent the message
	 */
	private void processRequestPoolListMessage(String[] aTokens, TorqueClientSocket aReceiver)
	{
		mLog.debug("Processing Request Pool List Message");
		if(aTokens.length == DominionClientProtocol.REQUEST_POOL_LIST_MSG_NUM_FIELDS)
		{
			CardStack[] lCards = mServer.getCardsInPool();
			aReceiver.sendMessageGuaranteed(createPoolListMessage(lCards));
		}
	}
}
