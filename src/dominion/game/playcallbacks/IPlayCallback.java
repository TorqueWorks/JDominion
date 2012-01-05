package dominion.game.playcallbacks;

import dominion.game.DominionGame;
import dominion.game.DominionPlayer;

/**
* Interface for creating a callback to play the card, applying various effects to the current game state. This should be used to create
* the different types of play effects and then applied to the appropriate cards.
*/

public interface IPlayCallback {

	/**
	 * Called when the card has been played. This is when a card will apply any effects it has to the game state.
	 * 
	 * @param aGame The {@link DominionGame} this card is being played in
	 * @param aOwner The {@link DominionPlayer} that is playing this card
	 */
	public void play(DominionGame aGame, DominionPlayer aPlayer);
}
