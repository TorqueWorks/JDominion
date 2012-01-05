package dominion.server;


import java.io.IOException;

import org.apache.log4j.Logger;

import dominion.game.Card;
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
		addCardToPool(Cards.mCards.get(0), 30); //Copper
		addCardToPool(Cards.mCards.get(0), 30); //Silver
		addCardToPool(Cards.mCards.get(0), 20); //Gold
		addCardToPool(Cards.mCards.get(0), 20); //Estate
		addCardToPool(Cards.mCards.get(0), 20); //Duchy
		addCardToPool(Cards.mCards.get(0), 10); //Province
	}

	/**
	 * Adds a new card to the pool of available cards for this game.
	 * 
	 * @param aCard The Card we want to add to the pool
	 * @param aTotal
	 */
	private void addCardToPool(Card aCard, int aTotal)
	{
		try {
			mGame.addCardToPool(aCard, aTotal);
		} catch (DominionException e) {
			return;
		}
	}
	
	public void addPlayer(String aName)
	{
		DominionPlayer lPlayer = null;
		try {
			lPlayer = mGame.addPlayer(aName);
		} catch (DominionException e1) {
			mLog.error(e1.getMessage());
			return;
		}

		try {
			this.sendMessage(DominionServerProtocol.createNewPlayerMessage(lPlayer));
		} catch (IOException e) {
			mLog.error(e.getMessage());
			return;
		}
	}
}
