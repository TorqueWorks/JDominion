package dominion.server;


import java.io.IOException;

import org.apache.log4j.Logger;

import dominion.game.Card;
import dominion.game.CardStack;
import dominion.game.Cards;
import dominion.game.DominionException;
import dominion.game.DominionGame;
import dominion.game.DominionPlayer;

import torque.client.TorqueClientSocket;
import torque.server.TorqueNetworkServer;
import torque.sockets.SocketCallback;

public class DominionServer extends TorqueNetworkServer {
	
	private final Logger mLog = Logger.getLogger(DominionServer.class.getName());
	
	private GAME_STATE mState;
	
	public enum GAME_STATE {JOINING, CHOOSING, PLAYING, ENDED};
	
	private ServerPlayerHandler[] mPlayerHandlers = new ServerPlayerHandler[DominionGame.MAX_PLAYERS];
	private final DominionGame mGame = new DominionGame();
	
	/**
	 * Creates a new JDominion server. Clients may connect to this server to join the game hosted by the server.
	 * 
	 * @param aPort The port to listen on for client connections
	 * @throws IOException If an I/O error occurred while creating the server 
	 */
	public DominionServer(int aPort)
			throws IOException {
		super(aPort);
		initGame();
		mState = GAME_STATE.JOINING;
		openConnections();
	}

	/**
	 * Do any initializing of the game state, such as adding default cards, we wish to do before opening the game
	 */
	private void initGame()
	{
		/** TODO: Figure out what to do for this
		addCardToPool(Cards.mCards.get(0), 30); //Copper
		addCardToPool(Cards.mCards.get(1), 30); //Silver
		addCardToPool(Cards.mCards.get(2), 20); //Gold
		addCardToPool(Cards.mCards.get(3), 20); //Estate
		addCardToPool(Cards.mCards.get(4), 20); //Duchy
		addCardToPool(Cards.mCards.get(5), 10); //Province
		**/
	}

	/**
	 * Adds a new card to the pool of available cards for this game.
	 * 
	 * @param aCard The Card we want to add to the pool
	 * @param aTotal
	 */
	protected void addCardToPool(int aIndex, Card aCard, int aTotal)
	{
		mGame.addCardToPool(aIndex, aCard, aTotal);
		try {
			this.sendMessage(DominionServerProtocol.createCardChosenMessage(aIndex, aCard==null?Cards.NULL_CARD_ID:aCard.getID()));
		} catch (IOException e) {
			mLog.error(e.getMessage());
		}
	}
	/**
	 * Attempts to add a player to the game. If the player was added successfully will also send a NEW PLAYER message out to all clients connected to the game
	 * to inform them of this historic event.
	 * 
	 * @param aName The name of the player who wishes to join
	 * @param aIsAdmin Whether the player is an admin or not
	 * @param aHandler The {@link ServerPlayerHandler} for the player wishing to join
	 * @return The player object for the new player
	 * @throws DominionException 
	 */
	protected DominionPlayer addPlayer(String aName, boolean aIsAdmin, ServerPlayerHandler aHandler) throws DominionException
	{
		DominionPlayer lPlayer = null;
		lPlayer = mGame.addPlayer(aName, aIsAdmin);
		
		sendMessageToAllConnectedClients(DominionServerProtocol.createNewPlayerMessage(lPlayer));
		mPlayerHandlers[lPlayer.getID()] = aHandler;

		
		return lPlayer;
	}
	
	/**
	 * Starts the game. This will only do something if the game is currently in the JOINING state.
	 * Events that occur:
	 * <ol>
	 * <li>Players are given their initial set of cards</li>
	 * <li>The player who goes first is chosen</li>
	 * <li>The game begins play</li>
	 * </ol>
	 */
	protected void startGame()
	{
		if(mState == GAME_STATE.JOINING)
		{
			try
			{
				sendMessage(DominionServerProtocol.createStartGameMessage());
			}
			catch(IOException ioe)
			{
				//TODO: Ignore for now...
			}
		}
	}
	
	/**
	 * Returns a mapping of the cards in the pool for the game running on this server. Note that this mapping is a copy
	 * of the original so modifications can be made without affecting the game.
	 * @return
	 */
	protected CardStack[] getCardsInPool()
	{
		return mGame.getCardsInPool();
	}

	@Override
	protected SocketCallback newConnection(TorqueClientSocket aSocket) {
		ServerPlayerHandler lHandler = new ServerPlayerHandler(this, aSocket);
		return lHandler;
	}
	
	/**
	 * Sends a message to all clients currently connected to this game. Note this is not all clients
	 * connected to the server, they have to actually be joined in the game to receive the message.
	 * 
	 * @param aMessage The message to send
	 */
	private void sendMessageToAllConnectedClients(String aMessage)
	{
		for(ServerPlayerHandler lHandler : mPlayerHandlers)
		{
			if(lHandler != null)
			{
				lHandler.sendMessage(aMessage, true);
			}
		}
	}
	
}
