package dominion.game;

import java.util.Arrays;

import dominion.game.playcallbacks.IPlayCallback;
import dominion.game.victorypointcallbacks.IVictoryPointCallback;


public class Card {

	/**
	 * The different types a card can be. This is used to determine when this card can be played as well as for interaction with other cards.
	 *
	 */
	public enum CARD_TYPE {
		ACTION(0), ATTACK(1), REACTION(3), VICTORY(4), TREASURE(5);
		
		private int mID;
		
		CARD_TYPE(int aID)
		{
			mID = aID;
		}
		public int getID()
		{
			return mID;
		}
	};

	
	private final String mName;
	private final int mCost;
	private final CARD_TYPE[] mTypes;
	private final IPlayCallback mPlayCallback;
	private final IVictoryPointCallback mVictoryPointCallback;
	public final int mID;
	
	
	/**
	 * A new Dominion card.
	 * 
	 * @param aName The name of the card. Used only for display purposes
	 * @param aCost The cost (in coins) of the card. Any value < 0 will be made 0.
	 * @param aTypes The list of {@link CARD_TYPE} that describe this card
	 * @param aPlayCallback The callback which is used when this card is played
	 * @param aID The unique ID for this card
	 */
	protected Card(String aName, int aCost, CARD_TYPE[] aTypes, IPlayCallback aPlayCallback, IVictoryPointCallback aVictoryPointCallback, int aID)
	{
		mName = aName;
		mCost = aCost < 0 ? 0 : aCost; //Only values > 0 are allowed, if < 0 make the value 0
		mTypes = Arrays.copyOf(aTypes, aTypes.length);
		mPlayCallback = aPlayCallback;
		mVictoryPointCallback = aVictoryPointCallback;
		mID = aID;
	}
	
	/**
	 * Plays the card, applying any of its effects onto the current game state
	 * 
	 * @param aGame The game this card is being played in
	 */
	protected void play(DominionGame aGame, DominionPlayer aPlayer)
	{
		mPlayCallback.play(aGame, aPlayer);
	}
	
	
	protected int calculateVictoryPoints(DominionGame aGame, DominionPlayer aPlayer)
	{
		return mVictoryPointCallback.calculateVictoryPoints(aGame, aPlayer);
	}
	

	/**
	 * A {@link Card} is equal to another Card if their IDs are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (mID != other.mID)
			return false;
		return true;
	}

	/**
	 * Gets the name of this card. The name is used for display purposes only, the ID is used to
	 * identify the individual cards.
	 * 
	 * @return The name of this card
	 */
	public String toString() {
		return mName;
	}

	/**
	 * Gets the ID of this card. The ID is used as a unique identifier for different types of cards that can be played.
	 * @return The unique ID of this card
	 */
	public int getID() {
		return mID;
	}
	
	/**
	 * Gets the cost of this card.
	 * 
	 * @return The cost of this card
	 */
	public int getCost()
	{
		return mCost;
	}
	
	/**
	 * Returns an array of all the {@link CARD_TYPE} of card that this card is. 
	 * 
	 * @return An array of CARD_TYPE for this card
	 */
	public CARD_TYPE[] getTypes()
	{
		return mTypes.clone();
	}
	
	/**
	 * Gets the name of this card in the form <i>name(ID)</i><br/>
	 * ex. Copper(0)
	 * 
	 * @return The name of this card in the form <i>name(ID)</i>
	 */
	public String getPrintName()
	{
		return mName + "(" + mID + ")";
	}
}
