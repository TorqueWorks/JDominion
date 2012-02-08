package torque.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ServerSocketFactory;

import org.apache.log4j.Logger;

import torque.sockets.SocketCallback;

public class TorqueNetworkServer {

	private TorqueServerSocket mServerSocket = null;
	
	private static final Logger mLog = Logger.getLogger(TorqueNetworkServer.class.getName());
	
	/**
	 * Creates a server
	 * @param aPort
	 * @param aCallback
	 * @throws IOException
	 */
	public TorqueNetworkServer(int aPort, SocketCallback aCallback) throws IOException {
		mServerSocket = new TorqueServerSocket(aPort, aCallback);
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

}
