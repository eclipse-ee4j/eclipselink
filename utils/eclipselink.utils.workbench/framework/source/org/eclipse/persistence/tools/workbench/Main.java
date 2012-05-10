/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkApplication;
import org.eclipse.persistence.tools.workbench.uitools.GlobalAWTExceptionHandler;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.ExceptionBroadcaster;
import org.eclipse.persistence.tools.workbench.utility.ExceptionHandlerThreadGroup;
import org.eclipse.persistence.tools.workbench.utility.ExceptionListener;
import org.eclipse.persistence.tools.workbench.utility.SimpleExceptionBroadcaster;
import org.eclipse.persistence.tools.workbench.utility.log.LoggingExceptionListener;


/**
 * Starting point for typical user of the TopLink Workbench.
 * This class will parse any command line arguments and launch the
 * Framework Application with the appropriate logger and
 * the set of preferences. The application will be launched
 * in its own thread [and thread group].
 * 
 * Current command-line arguments:
 *     [-open projectFile]
 *     [-dev]
 * 
 * This class builds the objects that should be in place before launching
 * the application; specifically, the thread group must be in place.
 */
public class Main {

	/**
	 * Launch the Framework Application.
	 */
	public static void main(String[] args) {
		new Main().execute(args);
	}


	/**
	 * Default constructor.
	 */
	private Main() {
		super();
	}

	/**
	 * Put in the various hooks necessary to catch all the "uncaught"
	 * exceptions that might occur while the application is executing.
	 * These hooks will forward those exceptions to the same logger
	 * that is passed to the application.
	 */
	private void execute(String[] args) {
		// parse the command line arguments
		File projectFile = this.projectFile(args);
		boolean developmentMode = this.developmentMode(args);

		// build the shared logger
		Logger logger = this.buildLogger();
		ExceptionListener listener = new LoggingExceptionListener(logger, Level.SEVERE, "UNEXPECTED_EXCEPTION");
		ExceptionBroadcaster broadcaster = new SimpleExceptionBroadcaster();
		broadcaster.addExceptionListener(listener);

		// put the exception handler hooks in place
		ThreadGroup threadGroup = new ExceptionHandlerThreadGroup("application", broadcaster);
		GlobalAWTExceptionHandler.register(broadcaster);

		// get the appropriate preferences node
		Preferences preferences = this.buildPreferences();

		// launch the application - this thread will only live until
		// the app is launched and its windows opened,
		// at which point the AWT EventDispatchThread will take over
		new Thread(threadGroup, new ApplicationLauncher(logger, preferences, projectFile, developmentMode), "Main").start();
	}

	/**
	 * Return the project file the user would like to open upon start-up.
	 */
	private File projectFile(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.toLowerCase().equals("-open")) {
				int j = i + 1;
				if (j < args.length) {
					return new File(args[j]);
				}
			}
		}
		return null;
	}

	/**
	 * Return whether to simulate development mode.
	 */
	private boolean developmentMode(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.toLowerCase().equals("-dev")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Build a logger with a name based on our package name.
	 * This logger will log to the standard error stream
	 * (typically the console).
	 */
	private Logger buildLogger() {
		Logger logger = Logger.getLogger(this.buildLoggerName(), FrameworkApplication.getResourceBundleName());
		return logger;
	}

	/**
	 * Return a logger name based on the jdk logging "guidelines".
	 */
	private String buildLoggerName() {
		return ClassTools.packageNameFor(this.getClass());
	}

	/**
	 * Build a preferences node with a name based on our package name.
	 */
	private Preferences buildPreferences() {
		Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		return preferences;
	}


	// ********** nested class **********

	/**
	 * Define a command that will launch the application.
	 */
	private static class ApplicationLauncher implements Runnable {
		private Logger logger;
		private Preferences preferences;
		private File projectFile;
		private boolean developmentMode;

		ApplicationLauncher(Logger logger, Preferences preferences, File projectFile, boolean developmentMode) {
			super();
			this.logger = logger;
			this.preferences = preferences;
			this.projectFile = projectFile;
			this.developmentMode = developmentMode;
		}

		public void run() {
			FrameworkApplication.launch(this.logger, this.preferences, this.projectFile, this.developmentMode);
		}

	}

}
