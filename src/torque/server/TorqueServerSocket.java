package torque.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.net.ServerSocketFactory;

import org.apache.log4j.Logger;

import torque.client.TorqueClientSocket;
import torque.sockets.SocketCallback;

public class TorqueServerSocket{
	
	private ServerSocket mServerSocket = null;
	private SocketCallback mCallback = null;
	
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
	 * @param aCallback The callback used to process messages
	 * @throws IOException If an error occured while making the socket
	 */
	public TorqueServerSocket(int aPort, SocketCallback aCallback) throws IOException{
		mServerSocket = ServerSocketFactory.getDefault().createServerSocket(aPort);
		mCallback = aCallback;
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
	 * @throws IOException If an error occurred while sending one of the messages
	 */
	public void sendMessageToAllClients(String aMessage) throws IOException {
		for(int i = 0; i < mClientSocketThreads.size(); i++) {
			mClientSocketThreads.get(i).sendMessage(aMessage);
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
					TorqueClientSocket cst = new TorqueClientSocket(mServerSocket.accept(), mCallback);
					mLog.debug("Connection accepted on port " + cst.getLocalPort() + " from " + cst.getRemoteAddress() + ":" + cst.getRemotePort());
					Thread.sleep(1000);  //Sleep for a second to allow the connection to initialize
					new Thread(cst).start(); //Create a new thread to run the ClientSocket and start it...
					synchronized (mClientSocketThreads)
					{
						mClientSocketThreads.add(cst);
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
