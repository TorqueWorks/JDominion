package torque.sockets;

import torque.client.TorqueClientSocket;

public interface SocketCallback {

	/**
	 * Called when a socket receives a complete message. 
	 * 
	 * @param aMessage The message that was received
	 * @param aReceiver The socket this message came in on
	 */
	public void processMessage(String aMessage, TorqueClientSocket aReceiver);
}
