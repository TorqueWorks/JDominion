package dominion.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import dominion.game.DominionException;
import dominion.game.DominionGame;
import dominion.game.DominionPlayer;

import torque.client.TorqueNetworkClient;
import torque.sockets.SocketCallback;

public class DominionClient extends TorqueNetworkClient{

	private DominionGame mGame = new DominionGame();
	
	private boolean mIsAdmin = false;
	private int mPlayerID = -1;
	
	private DominionClientWindow mWindow;
	
	/**
	 * A client for the JDominion game. This client connects to a JDominion server and provides a GUI to interact with the game.
	 * @param aPort The port the server is listening on
	 * @param aIPAddress The IPAddress/Hostname of the server to connect to
	 * @param aProtocol The protocol used to handle incoming messages
	 * @throws UnknownHostException Indicates that the IPAddress/Hostname could not be resolved
	 * @throws IOException If a general I/O error occurred while connecting to the server
	 */
	public DominionClient(int aPort, String aIPAddress, DominionClientProtocol aProtocol, boolean aIsAdmin)
			throws UnknownHostException, IOException {
		super(aPort, aIPAddress, aProtocol);
		mIsAdmin = aIsAdmin;
		aProtocol.setClient(this);
		this.sendMessage(DominionClientProtocol.createJoinGameMessage("zomg", aIsAdmin));
		mWindow = new DominionClientWindow(this);
	}
	
	/**
	 * Returns whether this client is an admin or not
	 * 
	 * @return <code>TRUE</code> if the client is an admin, <code>FALSE</code> if not
	 */
	protected boolean isAdmin()
	{
		return mIsAdmin;
	}
	
	protected int getPlayerID()
	{
		return mPlayerID;
	}
	
	protected void setPlayerID(int aPlayerID)
	{
		mPlayerID = aPlayerID;
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
	 * Prints a message to the UI for this client.
	 * 
	 * @param aMessage The message to display
	 */
	protected void displayMessage(String aMessage)
	{
		mWindow.displayMessage(aMessage);
	}

}
