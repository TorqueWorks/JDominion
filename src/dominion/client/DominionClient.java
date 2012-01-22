package dominion.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import dominion.game.Card;
import dominion.game.DominionException;
import dominion.game.DominionGame;
import dominion.game.DominionPlayer;
import dominion.game.DominionGame.CardStack;

import torque.client.TorqueNetworkClient;
import torque.sockets.SocketCallback;

public class DominionClient extends TorqueNetworkClient{

	private final DominionGame mGame = new DominionGame();
	private DominionPlayer mMe;
	
	private DominionClientWindow mWindow;
	
	private static final Logger mLog = Logger.getLogger(DominionClient.class.getName());
	
	/**
	 * A client for the JDominion game. This client connects to a JDominion server and provides a GUI to interact with the game.
	 * @param aPort The port the server is listening on
	 * @param aIPAddress The IPAddress/Hostname of the server to connect to
	 * @param aProtocol The protocol used to handle incoming messages
	 * @param aIsAdmin Whether this client is an admin client or not
	 * @param aName The name displayed for this client
	 * @throws UnknownHostException Indicates that the IPAddress/Hostname could not be resolved
	 * @throws IOException If a general I/O error occurred while connecting to the server
	 */
	public DominionClient(int aPort, String aIPAddress, DominionClientProtocol aProtocol, boolean aIsAdmin, String aName)
			throws UnknownHostException, IOException {
		super(aPort, aIPAddress, aProtocol);
		aProtocol.setClient(this);
		mMe = new DominionPlayer(aName, -1, aIsAdmin, mGame);
		mWindow = new DominionClientWindow(this);
		mWindow.displayMessage(DominionClientWindow.TOKEN_JOINING_GAME);
		this.sendMessage(DominionClientProtocol.createJoinGameMessage(aName, aIsAdmin));
	}
	

	/**
	 * Sets the ID for this client
	 * 
	 * @param aID
	 */
	protected void setID(int aID)
	{
		mLog.debug("Setting ID for client - " + aID);
		mMe.setID(aID);
	}
	/**
	 * Returns whether this client is an admin or not
	 * 
	 * @return <code>TRUE</code> if the client is an admin, <code>FALSE</code> if not
	 */
	protected boolean isAdmin()
	{
		return mMe.isAdmin();
	}
	
	/**
	 * Gets the game-unique player ID for this client
	 * @return
	 */
	protected int getID()
	{
		return mMe.getID();
	}
	
	/**
	 * Adds a player with the specified ID to the game. 
	 * 
	 * @param aName The name of the player to add
	 * @param aID The ID of the player to add
	 * @throws DominionException If the player couldn't be added for any reason
	 */
	public void addPlayer(String aName, int aID) throws DominionException
	{
		mGame.addPlayer(aName, aID, false); //Client doesn't care about storing other players admin status so just set to false
		mWindow.displayMessage("Player " + aName + " has joined!");
	}
	
	/**
	 * Adds a new card to the pool of available cards for this game. Updates the UI to display the new card
	 * as well.
	 * 
	 * @param aIndex The index of the pool to add the card at
	 * @param aCard The card to add to the pool
	 * @param aTotal The amount of this card to add to the pool
	 * @throws DominionException If the card was unable to be added to the pool
	 */
	protected void addCardToPool(int aIndex, Card aCard, int aTotal) throws DominionException
	{
		mGame.addCardToPool(aIndex, aCard, aTotal);
		mWindow.refreshCardPool();
	}
	
	/**
	 * Returns a mapping of all the cards in the pool for the game this client is in.
	 * 
	 * @return
	 */
	protected CardStack[] getCardsInPool()
	{
		return mGame.getCardsInPool();
	}
	/**
	 * Prints a message to the UI for this client.
	 * 
	 * @param aMessage The message to display
	 */
	protected void displayMessage(String aMessage)
	{
		mWindow.displayMessage(aMessage);
	}

}
