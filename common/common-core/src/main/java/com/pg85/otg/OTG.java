package com.pg85.otg;

import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import lombok.Getter;

/**
 * Main entry-point. Used for logging and to access OTGEngine.
 * OTGEngine is implemented and provided by the platform-specific 
 * layer and holds any objects and methods used during a session. 
 */
public class OTG
{
	private static OTGEngine Engine;
	@Getter
	private static ILogger logger;

	private OTG() { }

	// Engine

	public static void startEngine(OTGEngine engine)
	{
		if (Engine != null)
		{
			throw new IllegalStateException("Engine is already set.");
		}

		Engine = engine;
		engine.onStart();
		logger = Engine.getLogger();
	}

	public static OTGEngine getEngine()
	{
		if (Engine == null)
		{
			throw new IllegalStateException("Engine is not started.");
		}
		return Engine;
	}

	public static void stopEngine()
	{
		Engine.onShutdown();
		Engine = null;
	}

	// Logging
	public static void log(LogLevel logLevel, LogCategory logCategory, String message)
	{
		if (logger != null)
		{
			logger.log(logLevel, logCategory, message);
		}
		if (Engine == null)
		{
			throw new IllegalStateException("Engine is not started, tried to log: " + message);
		}
		logger = Engine.getLogger();
	}

	public static void log(String message)
	{
		log(LogLevel.INFO, LogCategory.MAIN, message);
	}

}
