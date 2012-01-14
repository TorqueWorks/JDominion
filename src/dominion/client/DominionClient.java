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

	DominionGame mGame = new DominionGame();
		
	DominionClientWindow mWindow;
	
	/**
	 * A client for the JDominion game. This client connects to a JDominion server and provides a GUI to interact with the game.
	 * @param aPort The port the server is listening on
	 * @param aIPAddress The IPAddress/Hostname of the server to connect to
	 * @param aProtocol The protocol used to handle incoming messages
	 * @throws UnknownHostException Indicates that the IPAddress/Hostname could not be resolved
	 * @throws IOException If a general I/O error occurred while connecting to the server
	 */
	public DominionClient(int aPort, String aIPAddress, DominionClientProtocol aProtocol)
			throws UnknownHostException, IOException {
		super(aPort, aIPAddress, aProtocol);
		aProtocol.setClient(this);
		this.sendMessage(DominionClientProtocol.createJoinGameMessage("zomg"));
		mWindow = new DominionClientWindow(this);
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
		mGame.addPlayer(aName, aID);
		mWindow.mTextArea.append("Player " + aName + " has joined!\n");
	}

}
