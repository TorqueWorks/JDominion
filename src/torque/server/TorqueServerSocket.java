package torque.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.net.ServerSocketFactory;

import org.apache.log4j.Logger;

import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class TorqueServerSocket{
	
	private final ServerSocket mServerSocket;
	private final TorqueNetworkServer mServer;
	
	private static ArrayList<TorqueClientSocket> mClientSocketThreads = new ArrayList<TorqueClientSocket>();
	private static ArrayList<Thread> mAcceptorThreads = new ArrayList<Thread>();
	
	private static final int MAX_ACCEPTION_THREADS = 5;
	
	private boolean mConnectionsOpened = false;
	
	private static final Logger mLog = Logger.getLogger(TorqueServerSocket.class.getName());
	
	/**
	 * Creates a new server socket on the specified port with the specified callback
	 * being used to process messages that come in on this socket.
	 * 
	 * @param aPort The local port to use for this socket
	 * @param aServerSocketCallback The callback used to notify an object of events which occur
	 * @throws IOException If an error occured while making the socket
	 */
	public TorqueServerSocket(int aPort, TorqueNetworkServer aServer) throws IOException{
		mServerSocket = ServerSocketFactory.getDefault().createServerSocket(aPort);
		mServer = aServer;
	}

	/**
	 * Open a number of threads to listen for new connections on this socket equal to MAX_CONNECTIONS.
	 * Note this can only be called once, any later calls will be ignored.
	 */
	protected void openConnections(){
		synchronized(this)
		{
			if(mConnectionsOpened) return;
			mConnectionsOpened = true;
		}
		for(int i = 0; i < MAX_ACCEPTION_THREADS; i++) {
			Acceptor lAcceptor = new Acceptor();
			Thread lAcceptorThread = new Thread(null, lAcceptor, "AcceptorThread" + i);
			mAcceptorThreads.add(lAcceptorThread); //Create the new acceptor thread and start it...
			lAcceptorThread.start();
		}
	}
	
	/**
	 * Sends a message to all the open client sockets on this connection
	 * 
	 * @param aMessage The message to send
	 * @param aFlushBuffer Whether to flush the buffer after the message is written (send it immediately)
	 */
	public void sendMessageToAllClients(String aMessage, boolean aFlushBuffer) {
		for(int i = 0; i < mClientSocketThreads.size(); i++) {
			mClientSocketThreads.get(i).sendMessage(aMessage, aFlushBuffer);
		}
	}
	
	/**
	 * Sends a guaranteed message to all the open client sockets on this connection. If an error
	 * occurs when sending one of the messages that message will be retried until it succeeds.
	 * 
	 * @param aMessage The message to send
	 */
	public void sendMessageToAllClientsGuaranteed(String aMessage)
	{
		for(int i = 0; i < mClientSocketThreads.size(); i++) {
			mClientSocketThreads.get(i).sendMessageGuaranteed(aMessage);
		}
	}
	
	/**
	 * A simple class which listens for a new connection on the port. When a connection is
	 * made a new ClientSocketThread is created and started.
	 *
	 */
	private class Acceptor implements Runnable{
		public void run() {
			try 
			{
				while(!mServerSocket.isClosed())
				{ //Keep accepting connections as long as the socket is open
					mLog.debug("Listening for connection on Port " + mServerSocket.getLocalPort() + "...");
					TorqueClientSocket lSocket = new TorqueClientSocket(mServerSocket.accept());
					mLog.debug("Connection accepted on port " + lSocket.getLocalPort() + " from " + lSocket.getRemoteAddress() + ":" + lSocket.getRemotePort());
					Thread.sleep(1000);  //Sleep for a second to allow the connection to initialize
					lSocket.setSocketCallback(mServer.newConnection(lSocket));
					lSocket.start();
					synchronized (mClientSocketThreads)
					{
						mClientSocketThreads.add(lSocket);
					}
				}
			} 
			catch(IOException ioe) 
			{
				ioe.printStackTrace();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
