package dominion.game.victorypointcallbacks;

import dominion.game.DominionGame;
import dominion.game.DominionPlayer;

/**
 * Interface for creating a callback to calculate the Victory Point value of a card. This should be used to create
 * the different types of Victory Point calculations and then applied to the appropriate cards.
 * 
 * @author Overlord
 *
 */
public interface IVictoryPointCallback {

	/**
	 * Calculates the Victory Points this card is worth based on the current game state.
	 * 
	 * @param aGame The game this card is in
	 * @param aPlayer The player this card belongs to
	 */
	public int calculateVictoryPoints(DominionGame aGame, DominionPlayer aPlayer);
}
