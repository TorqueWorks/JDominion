package dominion.server;

import java.io.IOException;

import org.junit.*;


import dominion.server.DominionServer;
import dominion.server.DominionServerProtocol;

public class UT_DominionServer {

	private DominionServer mServer;
	
	
	@Before public void setup() throws IOException
	{
		mServer = new DominionServer(1337, new DominionServerProtocol());
	}
	
	@After
	public void tearDown()
	{
		mServer = null;
	}
	
	@Test
	public void testAddPlayer()
	{
		mServer.addPlayer("Bob");
	}
	
}
