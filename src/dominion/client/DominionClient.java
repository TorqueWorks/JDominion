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
	
	public DominionClient(int aPort, String aIPAddress, DominionClientProtocol aProtocol)
			throws UnknownHostException, IOException {
		super(aPort, aIPAddress, aProtocol);
		aProtocol.setClient(this);
		this.sendMessage(DominionClientProtocol.createJoinGameMessage("zomg"));
		mWindow = new DominionClientWindow(this);
	}
	
	public void addPlayer(String aName, int aID) throws DominionException
	{
		mGame.addPlayer(aName, aID);
		mWindow.mTextArea.append("Player " + aName + " has joined!\n");
	}

}
