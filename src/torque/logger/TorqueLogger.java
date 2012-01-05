package torque.logger;

public class TorqueLogger {

	private static TorqueLogger mLogger = null;
	
	private boolean mDebug = false;
	
	public enum LOGGING_LEVEL {INFO, WARNING, ERROR};
	
	private TorqueLogger()
	{
		//Singleton implementation
	}
	
	public static TorqueLogger getInstance()
	{
		if(mLogger == null) 
		{ //Lazy instantiation
			mLogger = new TorqueLogger();
		}
		return mLogger;
	}
	
	public void logMessage(LOGGING_LEVEL aLevel, String aClass, String aMethod, String aMessage)
	{
		if(!mDebug) return;
		System.out.printf("%s::%s %s - %s\n",aClass,aMethod,aLevel.toString(),aMessage);
	}
	
	public void setDebug(boolean aDebug)
	{
		mDebug = aDebug;
	}
}
