package dominion.game;

public class CardStack
{
	private final Card mCard;
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
}