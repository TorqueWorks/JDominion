package dominion.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import dominion.server.DominionServer;

public class DominionPlayer {

	private final String mName;
	private final int mID;
	private final DominionGame mGame;
	private final boolean mIsAdmin;
	
	private final List<Card> mDeck = new LinkedList<Card>();
	private final List<Card> mDiscard = new LinkedList<Card>();
	private final List<Card> mHand = new ArrayList<Card>();
	
	private final Logger mLog = Logger.getLogger(DominionServer.class.getName());
	/**
	 * Creates a new DominionPlayer.
	 * 
	 * @param aName The name of this player. This is purely for display purposes, the ID is used to identify the player
	 * @param aID The game-unique identifier
	 * @param aIsAdmin Whether this player is an admin or not
	 * @param aGame The game this player is playing in
	 */
	protected DominionPlayer(String aName, int aID, boolean aIsAdmin, DominionGame aGame)
	{
		mName = aName;
		mID = aID;
		mGame = aGame;
		mIsAdmin = aIsAdmin;
	}
	
	protected boolean isAdmin() {
		return mIsAdmin;
	}

	/**
	 * Gets the name of this player. Note that the name is note used to identify the player in any way, use {@link #getID()} for that.
	 * @return The string representing the name of the this player.
	 */
	public String getName()
	{
		return mName;
	}
	
	/**
	 * Gets the game-unique identifier of this player
	 * 
	 * @return The integer ID value to identify this player
	 */
	public int getID()
	{
		return mID;
	}
	
	/**
	 * Adds a card to the top of this players deck (position 0).
	 * 
	 * @param aCard The card we're adding
	 * @throws DominionException If the card was null
	 */
	protected void addCardToDeck(Card aCard) throws DominionException
	{
		if(aCard == null) throw new DominionException("DominionPlayer::addCardToDeck", "Card for Player " + getPrintName() + " is null");

		mLog.debug("Adding Card " + aCard.getPrintName() + " to deck of player " + getPrintName());
		mDeck.add(0, aCard);
	}
	
	/**
	 * Adds a card to this players hand.
	 * 
	 * @param aCard The card we're adding
	 * @throws DominionException If the card was null
	 */
	protected void addCardToHand(Card aCard) throws DominionException
	{
		if(aCard == null) throw new DominionException("DominionPlayer::addCardToHand", "Card for Player " + getPrintName() + " is null");
		
		mLog.debug("Adding Card " + aCard.getPrintName() + " to hand of player " + getPrintName());
		mHand.add(aCard);
	}
	
	/**
	 * Adds a card to the top of this players discard pile (position 0).
	 * 
	 * @param aID The {@link Card} object we're adding
	 * @throws DominionException  If the card was null
	 */
	protected void addCardToDiscardPile(Card aCard) throws DominionException
	{
		if(aCard == null) throw new DominionException("DominionPlayer::addCardToDiscardPile", "Card for Player " + getPrintName() + " is null");

		mLog.debug("Adding Card " + aCard.getPrintName() + " to discard pile of player " + getPrintName());
		mDiscard.add(0, aCard);
	}
	
	/**
	 * Gets the number of cards in this players hand.
	 * 
	 * @return The number of cards in this players hand.
	 */
	public int getHandSize()
	{
		return mHand.size();
	}
	/**
	 * Gets the name of this player in the form of <i>name(ID)</i>
	 * @return
	 */
	public String getPrintName()
	{
		return mName + "(" + mID + ")";
	}
}
