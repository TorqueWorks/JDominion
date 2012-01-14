package torque.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import torque.sockets.SocketCallback;

public class TorqueNetworkClient {
	
		private TorqueClientSocket mClientSocketThread = null;
		
		/**
		 * Creates a new client which will attempt to open a connection to the specified remote host on the specified
		 * port. The callback will be used to process messages which are received from that connection.
		 * 
		 * @param aPort The remote port to connect to
		 * @param aIPAddress The remote address to connect to
		 * @param aCallback The callback object to use to handle messages received on the socket connection
		 * @throws UnknownHostException
		 * @throws IOException
		 */
	public TorqueNetworkClient(int aPort, String aIPAddress, SocketCallback aCallback) throws UnknownHostException, IOException {
		mClientSocketThread = new TorqueClientSocket(aPort, aIPAddress, aCallback);
		new Thread(mClientSocketThread).start();
	}
	
	/**
	 * Sends a message out on the socket this client is connected to
	 * 
	 * @param aMessage The message to be sent
	 * @throws IOException If an error occurred while trying to send the message
	 */
	public void sendMessage(String aMessage) throws IOException 
	{
		mClientSocketThread.sendMessage(aMessage);
	}
	
	/**
	 * Closes the underlying socket connection.
	 * 
	 * @throws IOException If an I/O error occured while attempting to close the socket
	 */
	public void closeSocket() throws IOException
	{
		mClientSocketThread.closeSocket();
	}
}
