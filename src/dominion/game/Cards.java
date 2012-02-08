package dominion.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dominion.game.Card.CARD_TYPE;

public final class Cards {

	public static final int NULL_CARD_ID = -1;
	/**
	 * Array of card objects. This is the only place they're defined, everywhere else uses the ID of the card and then references this
	 * array to get the definition of the card. 
	 * 
	 */
	public final static List<Card> mCards = Collections.unmodifiableList(Arrays.asList(
		new Card("Copper", 0, new CARD_TYPE[]{CARD_TYPE.TREASURE}, null, null, 0),
		new Card("Silver", 3, new CARD_TYPE[]{CARD_TYPE.TREASURE}, null, null, 1),
		new Card("Gold", 6, new CARD_TYPE[]{CARD_TYPE.TREASURE}, null, null, 2),
		
		new Card("Estate", 2, new CARD_TYPE[]{CARD_TYPE.VICTORY}, null, null, 3),
		new Card("Duchy", 5, new CARD_TYPE[]{CARD_TYPE.VICTORY}, null, null, 4),
		new Card("Province", 8, new CARD_TYPE[]{CARD_TYPE.VICTORY}, null, null, 5),
		
		new Card("Adventurer", 6, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 6),
		new Card("Bureaucrat", 4, new CARD_TYPE[]{CARD_TYPE.ACTION, CARD_TYPE.ATTACK}, null, null, 7),
		new Card("Cellar", 2, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 8),
		new Card("Chancellor", 3, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 9),
		new Card("Chapel", 2, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 10),
		new Card("Council Room", 5, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 11),
		new Card("Feast", 4, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 12),
		new Card("Festival", 5, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 13),
		new Card("Gardens", 4, new CARD_TYPE[]{CARD_TYPE.VICTORY}, null, null, 14),
		new Card("Laboratory", 5, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 15),
		new Card("Library", 5, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 16),
		new Card("Market", 5, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 17),
		new Card("Militia", 4, new CARD_TYPE[]{CARD_TYPE.ACTION, CARD_TYPE.ATTACK}, null, null, 18),
		new Card("Mine", 5, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 19),
		new Card("Moat", 2, new CARD_TYPE[]{CARD_TYPE.ACTION, CARD_TYPE.REACTION}, null, null, 20),
		new Card("MoneyLender", 4, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 21),
		new Card("Remodel", 4, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 22),
		new Card("Smithy", 4, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 23),
		new Card("Spy", 4, new CARD_TYPE[]{CARD_TYPE.ACTION, CARD_TYPE.ATTACK}, null, null, 24),
		new Card("Thief", 4, new CARD_TYPE[]{CARD_TYPE.ACTION, CARD_TYPE.ATTACK}, null, null, 25),
		new Card("Throne Room", 4, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 26),
		new Card("Village", 3, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 27),
		new Card("Witch", 5, new CARD_TYPE[]{CARD_TYPE.ACTION, CARD_TYPE.ATTACK}, null, null, 28),
		new Card("Woodcutter", 3, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 29),
		new Card("Workshop", 3, new CARD_TYPE[]{CARD_TYPE.ACTION}, null, null, 30)
		));
	
	/**
	 * Gets the card with the specified ID. 
	 * @param aID
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public static Card getCardByID(int aID) throws DominionException
	{
		try
		{
			return mCards.get(aID);
		}
		catch(IndexOutOfBoundsException e)
		{
				throw new DominionException("Cards::getCardByID", "Invalid ID");
		}
	}
}
