package dominion.game;

public class CardStack
{
	private Card mCard;
	private int mTotal;

	public CardStack(Card aCard, int aTotal)
	{
		mCard = aCard;
		mTotal = aTotal;
	}

	/**
	 * Copy constructor
	 * @param aCopy
	 */
	public CardStack(CardStack aCopy)
	{
		mCard = aCopy.mCard;
		mTotal = aCopy.mTotal;
	}
	/**
	 * Gets the card this stack contains
	 * @return
	 */
	public Card getCard()
	{
		return mCard;
	}

	/**
	 * Gets the total number of this card left in the stack
	 * @return
	 */
	public int getTotal()
	{
		return mTotal;
	}

	/**
	 * Adds the difference to the total. Subtract cards by passing a negative number.
	 * @param aDiff
	 */
	protected void alterTotal(int aDiff)
	{
		mTotal += aDiff;
	}
	
	/**
	 * Sets the card that this stack contains.
	 * 
	 * @param aCard The card type this stack will contain
	 * @return Reference to this card stack
	 */
	public CardStack setCard(Card aCard)
	{
		mCard = aCard;
		return this;
	}
	
	/**
	 * Sets the total number of cards this stack contains. Will set to 0 if <= 0.
	 * 
	 * @param aTotal The number of cards this stack contains
	 * @return Reference to this card stack
	 */
	public CardStack setTotal(int aTotal)
	{
		mTotal = aTotal<=0?0:aTotal;
		return this;
	}
}