package main;

import torque.sockets.SocketCallback;

public class TextCallback implements SocketCallback {

	private String mName = "";
	
	public TextCallback(String aName) {
		mName = aName;
	}
	@Override
	public void processMessage(String aMessage) {
		System.out.println(mName + "::processInput " + aMessage);
		
	}

}
