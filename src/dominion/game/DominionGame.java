package dominion.game;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DominionGame {

	public static final int MAX_PLAYERS = 5;
	
	//Use a simple array here since I want to always keep the space reserved for the players (just set empty spots to null)
	//and since the PlayerID is the index in the array we want to make sure that will never change (which the list data structures
	//don't ensure)
	private DominionPlayer[] mPlayers = new DominionPlayer[MAX_PLAYERS];
	
	private Map<Card, Integer> mCardPool = new HashMap<Card, Integer>();
	
	private Logger mLog = Logger.getLogger(DominionGame.class.getName());
	
	/**
	 * Adds a new player to this game and assigns them an ID. This will always either return a valid player
	 * or throw an exception
	 * 
	 * @param aName The name of this player
	 * @param aIsAdmin Whether the player is an admin or not
	 * @return The {@link DominionPlayer} object created for the new player
	 * @throws DominionException If this game has reached the maximum number of players defined
	 */
	public DominionPlayer addPlayer(String aName, boolean aIsAdmin) throws DominionException
	{
		mLog.debug("Adding player \"" + aName + "\"");

		//TODO: Better methods of doing this. Maybe created linked list of integers representing free spots then just pop/push whenever
		//a spot is taken/freed?
		synchronized(mPlayers)
		{
			for(int i = 0; i < mPlayers.length; i++)
			{
				if(mPlayers[i] == null)
				{ //This spot is empty, we can put our new player here
					DominionPlayer lPlayer = new DominionPlayer(aName, i, aIsAdmin, this);
					mPlayers[i] = lPlayer;
					return lPlayer;
				}
	
			}
		}
		//We went through the entire a
		throw new DominionException("DominionGame::DominionPlayer","Maximum number of players reached for this game.");
	}
	
	/**
	 * Adds a player with a pre-assigned ID to this game. This method will always either return a valid {@link DominionPlayer}
	 * object or throw an exception
	 * 
	 * @param aName The name of this player
	 * @param aID The game-unique ID for this player
	 * @param aIsAdmin Whether the player is an admin or not
	 * @return The DominionPlayer object created for the new player
	 * @throws DominionException If the ID is invalid ( < 0 or >= MAX_PLAYERS) or a player with that ID already exists
	 */
	public DominionPlayer addPlayer(String aName, int aID, boolean aIsAdmin) throws DominionException
	{
		mLog.debug("Adding player \"" + aName + "\" with ID " + aID);
		if(aID < 0 || aID >= MAX_PLAYERS) throw new DominionException("DominionGame::addPlayer", "Invalid ID " + aID);
		if(mPlayers[aID] != null) throw new DominionException("DominionGame::addPlayer","Player with that ID already exists");
		
		DominionPlayer lPlayer = new DominionPlayer(aName, aID, aIsAdmin, this);
		mPlayers[aID] = lPlayer;
		return lPlayer;
	}
	
	/**
	 * Adds a card to the pool of available cards. If the card already exists in the pool the number of cards
	 * is instead added to the current number of cards of this type.
	 * 
	 * @param aCardID The ID of the card to add, see {@link Cards} for card definitions
	 * @param aTotal The number of this card available in the pool
	 * @throws DominionException 
	 */
	public void addCardToPool(Card aCard, int aTotal) throws DominionException
	{
		if(aCard == null) throw new DominionException("DominionGame::addCardToPool", "Card was null");
		
		mLog.debug("Adding " + aTotal + " of Card " + aCard.getPrintName() + " to pool");
		if(mCardPool.containsKey(aCard))
		{ //We already have copies of this card, just add these ones to the pool
			mCardPool.put(aCard, mCardPool.get(aCard) + aTotal);
		}
		else
		{
			mCardPool.put(aCard, aTotal);
		}
	}
	
	/**
	 * Attempts to give a card from the card pool to the specified player. These must all hold true for the player to receive the card:
	 * <ul>
	 * <li>The PlayerID must be valid</li>
	 * <li>The Card must exist in this game</li>
	 * <li>The pile for this card must not be empty</li>
	 * </ul>
	 * 
	 * @param aCard The card to give to the player
	 * @param aPlayerID The ID of the player to receive the card
	 * @throws DominionException If an error occurred which prevented the player from receiving the card
	 */
	public void giveCardToPlayer(Card aCard, int aPlayerID) throws DominionException
	{
		mLog.debug("Giving Card " + aCard.getPrintName() + " to Player " + aPlayerID);
		if(aPlayerID < 0 || aPlayerID >= MAX_PLAYERS) throw new DominionException("DominionGame::giveCardToPlayer","Invalid player ID");
		if(mCardPool.get(aCard) == null) throw new DominionException("DominionGame::giveCardToPlayer","Card does not exist in this game");
		if(mCardPool.get(aCard).equals(0)) throw new DominionException("DominionGame::giveCardToPlayer","Card pile is empty");
		
		DominionPlayer lPlayer = mPlayers[aPlayerID];
		if(lPlayer == null) throw new DominionException("DominionGame::giveCardToPlayer", "Player does not exist");
		lPlayer.addCardToDiscardPile(aCard);
		mCardPool.put(aCard, mCardPool.get(aCard) - 1);
		
	}
	
	/**
	 * Checks whether the player specified by the ID is an admin or not.
	 * 
	 * @param aPlayerID The ID of the player to check
	 * @return <code>TRUE</code> if the player is an admin, <code>FALSE</code> if not
	 */
	public boolean isPlayerAdmin(int aPlayerID)
	{
		if(aPlayerID >= 0 && aPlayerID < MAX_PLAYERS && mPlayers[aPlayerID] != null) return mPlayers[aPlayerID].isAdmin();
		return false;
	}
	
	/**
	 * Returns a mapping of all the cards in this pool along with the number of that card left in the pool.
	 * @return
	 */
	public HashMap<Card,Integer> getCardsInPool()
	{
		HashMap<Card,Integer> lRet = new HashMap<Card,Integer>();
		lRet.putAll(mCardPool);
		return lRet;
	}
}
