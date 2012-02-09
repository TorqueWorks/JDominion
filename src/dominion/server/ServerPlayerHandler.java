package dominion.server;

import dominion.game.Card;
import dominion.game.CardStack;
import dominion.game.DominionException;
import dominion.game.DominionPlayer;
import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class ServerPlayerHandler implements SocketCallback
{

	private DominionPlayer mPlayer;
	private final DominionServerProtocol mProtocol;
	private final TorqueClientSocket mConnection;
	private final DominionServer mServer;
	
	protected ServerPlayerHandler(DominionServer aServer, TorqueClientSocket aConnection) 
	{
		mConnection = aConnection;
		mServer = aServer;
		mProtocol = new DominionServerProtocol(this);
	}

	/**
	 * Has the player attempt to join the game being hosted by the server they're on. Returns the
	 * ID of the player in that game if successful, -1 if not.
	 * 
	 * @param aName The name to display for this user
	 * @param aIsAdmin Whether the user is an admin or not
	 * @return The game playerID for this player or -1 if the join was unsuccessful
	 */
	protected int joinGame(String aName, boolean aIsAdmin)
	{
		if(mPlayer != null)
		{ //We've already joined this game...just get the ID
			return mPlayer.getID();
		}
		int lPlayerID = -1;
		try {
			mPlayer = mServer.addPlayer(aName, aIsAdmin, this);
			lPlayerID = mPlayer.getID();
		} catch (DominionException ignore) {}
		return lPlayerID;
	}
	
	/**
	 * Returns a mapping of the cards in the pool for the game this player 
	 * @return
	 */
	protected CardStack[] getCardsInPool()
	{
		return mServer.getCardsInPool();
	}
	
	/**
	 * Checks whether this player is an admin
	 * 
	 * @return <code>TRUE</code> if the player is an admin, <code>FALSE</code> if not
	 */
	protected boolean isPlayerAdmin()
	{
		if(mPlayer != null)
		{
			return mPlayer.isAdmin();
		}
		return false;
	}
	
	/**
	 * If the player is an admin and is joined to a game then tells the server to start the game.
	 */
	protected void startGame()
	{
		if(mPlayer != null && mPlayer.isAdmin())
		{
			mServer.startGame();
		}
	}
	
	/**
	 * Adds a card to the pool of available cards. The card is added at the slot index specified
	 * with the specified number of total cards in the pile.
	 * 
	 * Only admin players are allowed this functionality. Calls to non-admin players will have
	 * no result.
	 * 
	 * @param aIndex The index to add the card at
	 * @param aCard The card to add
	 * @param aTotal The number of the card to add
	 */
	protected void addCardToPool(int aIndex, Card aCard, int aTotal)
	{
		if(mPlayer != null && mPlayer.isAdmin())
		{
			mServer.addCardToPool(aIndex, aCard, aTotal);
		}
	}
	
	/**
	 * Sends a message out on the connection for this client. The message is not guaranteed.
	 * @param aMessage The message to send
	 * @param aFlushBuffer Whether to flush the buffer after sending the message
	 */
	protected void sendMessage(String aMessage, boolean aFlushBuffer)
	{
		mConnection.sendMessage(aMessage, aFlushBuffer);
	}
	
	@Override
	public void processMessage(String aMessage, TorqueClientSocket aReceiver) {
		mProtocol.processMessage(aMessage, aReceiver);
	}
}
