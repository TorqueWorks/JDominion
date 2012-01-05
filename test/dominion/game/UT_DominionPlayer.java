package dominion.game;

import static org.junit.Assert.*;

import org.junit.*;

import dominion.game.DominionPlayer;

public class UT_DominionPlayer {

	private DominionPlayer mPlayer;
	
	@Before
	public void setup()
	{
		mPlayer = new DominionPlayer("Player1", 0, null);
	}
	
	@After
	public void tearDown()
	{
		mPlayer = null;
	}
	
	@Test
	public void testAddCardsToHand() throws DominionException
	{
		Card lCard;
		int lStartSize;
		int lEndSize;
		for(int i = 0; i < Cards.mCards.size(); i++)
		{ //Add one of each card to players hand
			lStartSize = mPlayer.getHandSize();
			lCard = Cards.mCards.get(i);
			assertNotNull(lCard); //Make sure card actually exists
			mPlayer.addCardToHand(lCard);
			lEndSize = mPlayer.getHandSize();
			assertEquals(lStartSize + 1, lEndSize); //Make sure hand size increased
		}
	}
}
