package torque.server;

import java.io.IOException;

import org.apache.log4j.Logger;

import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class TorqueNetworkServer {

	private TorqueServerSocket mServerSocket = null;
	
	private static final Logger mLog = Logger.getLogger(TorqueNetworkServer.class.getName());
	
	/**
	 * Creates a server
	 * @param aPort
	 * @throws IOException
	 */
	public TorqueNetworkServer(int aPort) throws IOException {
		mServerSocket = new TorqueServerSocket(aPort, this);
	}
	
	/**
	 * Sends a message to all clients connected to this server.
	 * 
	 * @param aMessage The message to send
	 * @throws IOException 
	 */
	public void sendMessage(String aMessage) throws IOException
	{
		mServerSocket.sendMessageToAllClients(aMessage, true);
	}
	
	/**
	 * Starts the connection threads for this server which listen for incoming connections and
	 * handles getting the connection initialized. Note this can only be called once, any
	 * further calls will do nothing.
	 */
	protected void openConnections()
	{
		mServerSocket.openConnections();
	}

	/**
	 * Called whenever we get a new connection. Contains the ClientSocket used to communicate with the client
	 * which connected to us.
	 * 
	 * This is also how you set the callback which is called for every new message
	 * 
	 * @param aConnection
	 */
	protected SocketCallback newConnection(TorqueClientSocket aConnection)
	{
		return null;
		//Do nothing unless overridden
	}
}
