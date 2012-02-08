package dominion.server;


import java.io.IOException;

import org.apache.log4j.Logger;

import dominion.game.Card;
import dominion.game.CardStack;
import dominion.game.Cards;
import dominion.game.DominionException;
import dominion.game.DominionGame;
import dominion.game.DominionPlayer;

import torque.server.TorqueNetworkServer;

public class DominionServer extends TorqueNetworkServer{
	
	private Logger mLog = Logger.getLogger(DominionServer.class.getName());
	
	private GAME_STATE mState;
	
	public enum GAME_STATE {JOINING, CHOOSING, PLAYING, ENDED};
	
	private DominionGame mGame = new DominionGame();
	
	/**
	 * Creates a new JDominion server. Clients may connect to this server to join the game hosted by the server.
	 * 
	 * @param aPort The port to listen on for client connections
	 * @param aProtocol The protocol object to use for parsing messages
	 * @throws IOException If an I/O error occurred while creating the server 
	 */
	public DominionServer(int aPort, DominionServerProtocol aProtocol)
			throws IOException {
		super(aPort, aProtocol);
		aProtocol.setServer(this);
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
		try {
			mGame.addCardToPool(aIndex, aCard, aTotal);
		} catch (DominionException e) {
			return;
		}
		try {
			this.sendMessage(DominionServerProtocol.createCardChosenMessage(aIndex, aCard.getID()));
		} catch (IOException e) {
			mLog.error(e.getMessage());
		}
	}
	/**
	 * Attempts to add a player to the game. If the player was added successfully will also send a NEW PLAYER message out to all clients connected to the game
	 * to inform them of this historic event.
	 * 
	 * @param aName The name of the player who wishes to join
	 * @return The player object for the new player
	 * @throws DominionException 
	 */
	protected DominionPlayer addPlayer(String aName, boolean aIsAdmin) throws DominionException
	{
		DominionPlayer lPlayer = null;
		lPlayer = mGame.addPlayer(aName, aIsAdmin);

		try {
			this.sendMessage(DominionServerProtocol.createNewPlayerMessage(lPlayer));
		} catch (IOException e) {
			mLog.error(e.getMessage());
		}
		return lPlayer;
	}
	
	/**
	 * Checks whether the player specified by the given ID is an admin or not.
	 * 
	 * @param aPlayerID The ID of the player to check
	 * @return <code>TRUE</code> if the player is an admin, <code>FALSE</code> if not
	 */
	protected boolean isPlayerAdmin(int aPlayerID)
	{
		return mGame.isPlayerAdmin(aPlayerID);
	}
	/**
	 * Starts the game. This will only do something if the game is currently in the JOINING state.
	 * Events that occur:
	 * <ol>
	 * <li>Cards for pool are chosen</li>
	 * <li>Players are given their initial set of cards</li>
	 * <li>The player who goes first is chosen</li>
	 * <li>The game begins play</li>
	 * </ol>
	 * @throws IOException 
	 */
	protected void startGame() throws IOException
	{
		//TODO: Have players choose cards
		int[] lChosenCards = new int[10];
		for(int i = 0; i < 10; i++)
		{
			try {
				mGame.addCardToPool(i, Cards.mCards.get(i + 6), 10);
			
			} catch (DominionException e) {
				mLog.error(e.getMessage());
			}
			lChosenCards[i] = i + 6;
		}
		sendMessage(DominionServerProtocol.createStartGameMessage());
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
	
}
