package dominion;

import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import dominion.game.UT_DominionPlayer;
import dominion.server.UT_DominionServer;

public class Tester {

	public static void main(String[] args)
	{
			runTest(UT_DominionPlayer.class);
			runTest(UT_DominionServer.class);
	}
	
	public static void runTest(Class<?> aClass)
	{
		Result lResult = org.junit.runner.JUnitCore.runClasses(aClass);
		List<Failure> lFailures = lResult.getFailures();
		System.out.printf("##########################################################\n");
		System.out.printf("##TEST FOR CLASS : " + aClass.getName() + "\n");
		System.out.printf("##########################################################\n\n");
		System.out.printf("Total Tests: %d\n", lResult.getRunCount());
		System.out.printf("Total FAILED Tests: %d (%.2f%%)\n\n", lResult.getFailureCount(), lResult.getFailureCount() * 100.0 / (float)lResult.getRunCount());
		for(Failure lFailure : lFailures)
		{
			System.out.printf("%s\n\t%s\n\n", lFailure.getTestHeader(), lFailure.getTrace());
		}
		System.out.printf("\n\n");
		System.out.flush();
	}
	
}
