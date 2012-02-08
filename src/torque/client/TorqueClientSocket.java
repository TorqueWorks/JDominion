package torque.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import torque.sockets.SocketCallback;

public class TorqueClientSocket implements Runnable{

	private Socket mSocket = null;
	private SocketCallback mSocketCallback = null;
	private InputStreamReader in = null;
	private OutputStreamWriter out = null;
	
	private int mInSTX, mInETX;
	private String mOutSTX, mOutETX;
	
	private boolean mHaveSTX = false;
	
	private StringBuffer lInBuffer = new StringBuffer();
	
	private final ConcurrentLinkedQueue<String> mMessageBuffer = new ConcurrentLinkedQueue<String>();
	private final Thread mGuaranteedMessageSenderThread = new GuaranteedMessageThread();
	
	private Logger mLog = Logger.getLogger(TorqueClientSocket.class.getName());
	
	public static final String DEFAULT_OUTPUT_ENCODING = "US-ASCII"; //Default character encoding for outgoing communication
	public static final String DEFAULT_INPUT_ENCODING = "US-ASCII"; //Default character encoding for incoming communication
	public static final int MAX_INPUT_BUFFER_SIZE = 1024; //Max size of the input buffer
	
	private static final int END_OF_STREAM = -1;
	/**
	 * Creates a new connection object which listens and writes on the socket passed in. The SocketCallback class
	 * is used to process input. Note this will used the default encoding DEFAULT_OUTPUT_ENCODING to encode
	 * outgoing communication with.
	 * 
	 * @param aIPAddress IPAddress/Hostname to connect to
	 * @param aPort Port to connect to
	 * @param aCallback The SocketCallback object used to process input
	 * @param aChannelID The ID used to identify which ClientSocket a message came in on
	 * @throws IOException
	 */
	public TorqueClientSocket(int aPort, String aIPAddress, SocketCallback aCallback) throws IOException
	{
		if(aCallback == null)
		{
			return; //TODO: Throw an exception or something probably
		}
		mSocket = new Socket(aIPAddress, aPort);
		mSocketCallback = aCallback;
		
		//TODO Don't hardcode these
		mInSTX = 0x02;
		mInETX = 0x03;
		mOutSTX = String.valueOf((char)0x02);
		mOutETX = String.valueOf((char)0x03);
		
		BufferedInputStream lbis = new BufferedInputStream(mSocket.getInputStream());
		in = new InputStreamReader(lbis, DEFAULT_INPUT_ENCODING);
		
		BufferedOutputStream lbos = new BufferedOutputStream(mSocket.getOutputStream());
		out = new OutputStreamWriter(lbos,DEFAULT_OUTPUT_ENCODING);
	}
	
	/**
	 * Creates a new connection object which listens and writes on the socket passed in. The SocketCallback class
	 * is used to process input. This will also encode any outgoing communication in the format passed in.
	 * 
	 * @param aIPAddress IPAddress/Hostname to connect to
	 * @param aPort Port to connect to
	 * @param aCallback The SocketCallback object used to process input
	 * @param aOutputEncoding The character encoding to use for outgoing communication
	 * @throws IOException
	 */
	public TorqueClientSocket(int aPort, String aIPAddress, SocketCallback aCallback, String aOutputEncoding) throws IOException
	{
		if (aCallback == null)
		{
			return; //TODO: Throw an exception or something
		}
		mSocket = new Socket(aIPAddress, aPort);
		mSocketCallback = aCallback;

		//TODO Don't hardcode these
		mInSTX = 0x02;
		mInETX = 0x03;
		mOutSTX = String.valueOf((char)0x02);
		mOutETX = String.valueOf((char)0x03);
		
		BufferedInputStream bis = new BufferedInputStream(mSocket.getInputStream());
		in = new InputStreamReader(bis, DEFAULT_INPUT_ENCODING);
		
		BufferedOutputStream bos = new BufferedOutputStream(mSocket.getOutputStream());
		out = new OutputStreamWriter(bos, aOutputEncoding);
		
	}

	/**
	 * Creates a new connection object which listens and writes on the socket passed in. The SocketCallback class
	 * is used to process input. This will also encode any outgoing communication in the format passed in.
	 * 
	 * @param aSocket The socket to communicate on
	 * @param aCallback The SocketCallback object used to process input
	 * @throws IOException
	 */
	public TorqueClientSocket(Socket aSocket, SocketCallback aCallback) throws IOException, InvalidParameterException
	{
		if(aSocket == null)
		{
			throw new InvalidParameterException("Socket cannot be null");
		}
		else if (aCallback == null)
		{
			throw new InvalidParameterException("SocketCallback cannot be null");
		}
		mSocket = aSocket;
		mSocketCallback = aCallback;
		
		//TODO Don't hardcode these
		mInSTX = 0x02;
		mInETX = 0x03;
		mOutSTX = String.valueOf((char)0x02);
		mOutETX = String.valueOf((char)0x03);
		
		BufferedInputStream bis = new BufferedInputStream(mSocket.getInputStream());
		in = new InputStreamReader(bis, DEFAULT_INPUT_ENCODING);
		
		BufferedOutputStream bos = new BufferedOutputStream(mSocket.getOutputStream());
		out = new OutputStreamWriter(bos,DEFAULT_OUTPUT_ENCODING);
	}
	/**
	 * Sets the character which will be used to determine the beginning of a message.
	 * If this character is encountered while reading input the buffer will be cleared
	 * and a new message will begin.
	 * 
	 * @param aNewSTX
	 */
	public void setInSTX(int aNewSTX)
	{
		mInSTX = aNewSTX;
	}
	
	/**
	 * Sets the character which will be appended to the beginning of messages going out.
	 * 
	 * @param aNewSTX
	 */
	public void setOutSTX(int aNewSTX)
	{
		mOutSTX = String.valueOf((char)aNewSTX);
	}

	
	/**
	 * Sets the character which will be used to determine the end of a message. When
	 * this character is encountered while reading input the SocketCallback class
	 * will be called to process the message and the buffer will be cleared.
	 * 
	 * @param aNewETX
	 */
	public void setETX(int aNewETX)
	{
		mInETX = aNewETX;
	}
	
	/**
	 * Sets the character which will be appended to the end of messages going out.
	 * @param aNewETX
	 */
	public void setOutETX(int aNewETX)
	{
		mOutETX = String.valueOf((char)aNewETX);
	}
	
	/**
	 * Constantly reads input from the socket until the EOS is reached or an error occurs. Calls the
	 * callback whenever a complete message is received
	 */
	@Override
	public void run() 
	{
		lInBuffer.setLength(0); //Reset our buffer
		int lReadChar;
		try {
			while((lReadChar = in.read()) != END_OF_STREAM) 
			{
				if(lReadChar == mInSTX) 
				{ //Start of message, clear buffer
					mLog.debug("Found STX");
					if(lInBuffer.length() != 0)
					{ //Uh-oh, still had stuff in the buffer. Print out what we're losing!
						mLog.debug("STX found with items in buffer, clearing. (" + lInBuffer.toString() + ")");
					}
					lInBuffer.setLength(0);
					mHaveSTX = true;
				}
				else if (lReadChar == mInETX) 
				{ //End of message, send contents of buffer to be processed then reset buffer
					mLog.debug("Found ETX (" + lInBuffer.toString() + ")");
					mSocketCallback.processMessage(lInBuffer.toString(), this);
					lInBuffer.setLength(0);
					mHaveSTX = false;
				}
				else
				{ //Something else, just append to the buffer and continue on...
					mLog.debug("Found other (" + (char)lReadChar + ")");
					if(!mHaveSTX)
					{ //Haven't gotten a STX yet, ignore input until we do
						mLog.debug("Got input but no STX, ignoring (" + (char)lReadChar + ")");
						continue;
					}
					lInBuffer.append((char)lReadChar);
					if(lInBuffer.length() > MAX_INPUT_BUFFER_SIZE)
					{ //Buffer is full and no ETX, better clear it!
						mLog.debug("Max input buffer size reached, clearing. (" + lInBuffer.toString() + ")");
						mHaveSTX = false;
					}
				}
				mLog.debug("Current buffer is (" + lInBuffer.toString() + ")");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mLog.debug("Socket closed");
	}
	
	/**
	 * Sends a message out on the socket connection. Note this WILL append the
	 * STX and ETX characters to the message before sending it out.
	 * 
	 * @param aMessage The message to send
	 * @param aFlushBuffer Whether to flush the buffer after this message is written
	 */
	public void sendMessage(String aMessage, boolean aFlushBuffer)
	{
		try
		{
			out.write(mOutSTX + aMessage + mOutETX);
			if(aFlushBuffer)
			{
				out.flush();
			}
		}
		catch(IOException ignore) {}

	}
	
	/**
	 * Sends a guaranteed message out on the socket connection. If an error occurs while
	 * trying to send the message it will retry until it is successfully sent. The message
	 * queue is synchronous.
	 * 
	 * Note this WILL append the STX and ETX to the message before sending it out
	 * 
	 * @param aMessage The message to send
	 */
	public void sendMessageGuaranteed(String aMessage)
	{
		mMessageBuffer.add(mOutSTX + aMessage + mOutETX);
		if(!mGuaranteedMessageSenderThread.isAlive())
		{ //Git 'er runnin'
			mGuaranteedMessageSenderThread.start();
		}
	}
	
	/**
	 * Returns the string representation of the remote IP connected to this socket.
	 * 
	 * @return The IP address of the remote host
	 */
	public String getRemoteAddress() 
	{
		return mSocket.getInetAddress().toString();
	}
	
	/**
	 * Returns the local port which this socket is bound to.
	 * 
	 * @return The local port number
	 */
	public int getLocalPort() 
	{
		return mSocket.getLocalPort();
	}
	
	/**
	 * Returns the remote port this socket is connected to.
	 * 
	 * @return The remote port number
	 */
	public int getRemotePort()
	{
		return mSocket.getPort();
	}
	/**
	 * Closes the socket connection
	 * @throws IOException
	 */
	public void closeSocket() throws IOException
	{
		mSocket.close();
	}
	
	private class GuaranteedMessageThread extends Thread
	{
		@Override
		public void run()
		{
			while(!mMessageBuffer.isEmpty())
			{
				try
				{
					out.write(mMessageBuffer.peek());
					out.flush();
				}
				catch(IOException ioe)
				{ //Error occurred, sleep a bit before trying again
					//TODO: Need better way of handling this
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {}
				}
				mMessageBuffer.remove(); //Successfully send message so remove it from the queue
			}
		}
	}
}
