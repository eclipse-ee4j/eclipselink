/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.framework.help;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Wrap up the OHJ stuff.
 */
public final class HelpFacade {

	// ********** constants **********

	/**
	 * The name of the "help" preferences node used to store
	 * preferences used by the help facade.
	 */
	private static final String HELP_PREFERENCES_NODE = "help";

	/**
	 * The name of the application's OHJ help set file.
	 * By default, this file should be found IN the tlmwhelp.jar;
	 * and tlmwhelp.jar should be on the classpath.
	 */
	private static final String HELP_SET_FILE_NAME = "/tlmwhelp.hs";
	
	private static final String HELP_JAR_NAME = "/tlmwhelp.jar";
	
	private static final String BASE_HELP_JAR_NAME = "/tlmwhelp";

	/**
	 * This should specify the file containing
	 * the *complete* Help content.
	 */
	static final String LOCAL_FILE_PREFERENCE = "local file";
		static final String LOCAL_FILE_PREFERENCE_DEFAULT = "";

	/** If true, the Help Welcome screen will be opened at start-up. */
	static final String DISPLAY_WELCOME_PREFERENCE = "display welcome";
		static final boolean DISPLAY_WELCOME_PREFERENCE_DEFAULT = true;

	/** flag to indicate that the local helpset selected in preferences by the user failed to load */
//	private static boolean localFailed = false;


	// ********** methods **********

	/**
	 * Build and return the appropriate help manager.
	 */
	public static HelpManager buildHelpManager(HelpManagerConfig config) {
		Preferences helpPreferences = config.getPreferences().node(HELP_PREFERENCES_NODE);
		return new DefaultHelpManager(config.getResourceRepository(), helpPreferences);
	}

	
	private static URL buildLocalizedHelpUrl(Logger logger) {
		//try with country first
		URL helpUrl = HelpFacade.class.getResource(localizedHelpResourceJarName(true));
		if (helpUrl == null) {
			//try now without country code
			helpUrl = HelpFacade.class.getResource(localizedHelpResourceJarName(false));
		}
		if (helpUrl != null) {
			try { 
				helpUrl = new URL("jar:" + helpUrl.toString() + "!/");
			} catch (MalformedURLException ex) {
				log(logger, "LOCALIZED_FILE_NOT_FOUND", helpUrl, ex);
			}
			URL[] urls = {helpUrl};
			URLClassLoader cl = new URLClassLoader(urls);
			helpUrl = cl.findResource(HELP_SET_FILE_NAME);
		}
		return helpUrl;
	}

	/**
	 * Returns the appropriate help Jar name for the default locale
	 */
	
	private static String localizedHelpResourceJarName(boolean includeCountryCode) {
		Locale locale = Locale.getDefault();
		StringBuffer sb = new StringBuffer();
		sb.append(BASE_HELP_JAR_NAME);
		sb.append("_");
		sb.append(locale.getLanguage());
		if (includeCountryCode && !StringTools.stringIsEmpty(locale.getCountry())) {
			sb.append("_");
			sb.append(locale.getCountry());
		}
		sb.append(".jar");
		return sb.toString();
	}

	/**
	 * Start a thread that will wait until the application has finished launching.
	 * This will allow us to return the Help Manager as soon as it is built.
	 * (It also prevents some deadlocks - see bug 3831450.)
	 */
	private static void startPostLaunchThread(InternalHelpManager helpManager, HelpManagerConfig config) {
		new Thread(buildPostLaunchRunnable(helpManager, config), "Display Welcome").start();
	}

	/**
	 * Build a Runnable that will wait until the application is finished launching;
	 * then it will display any error messages concerning Help set-up and display
	 * the stupid Help Welcome Screen, if appropriate.
	 */
	private static Runnable buildPostLaunchRunnable(final InternalHelpManager helpManager, final HelpManagerConfig config) {
		return new Runnable() {
			public void run() {
				try {
					config.getLaunchCompleteFlag().waitUntilTrue();
				} catch (InterruptedException ex) {
					// no need to display anything, just let the thread die
					return;
				}
				Preferences helpPreferences = config.getPreferences().node(HELP_PREFERENCES_NODE);
				if (config.forceStupidWelcomeScreen() ||
						helpPreferences.getBoolean(DISPLAY_WELCOME_PREFERENCE, DISPLAY_WELCOME_PREFERENCE_DEFAULT)) {
					helpManager.showTopic("welcome");
				}
				helpManager.launchComplete();
			}
		};
	}

	/**
	 * We need to do all this because Logger#log(LogRecord) does not pass through
	 * Logger#doLog(LogRecord) like all the other Logger#log(...) methods.
	 */
	private static void log(Logger logger, String message, Object parm, Throwable exception) {
		LogRecord logRecord = new LogRecord(Level.WARNING, message);
		logRecord.setParameters(new Object[] {parm});
		logRecord.setThrown(exception);
		logRecord.setLoggerName(logger.getName());
		logRecord.setResourceBundle(logger.getResourceBundle());
		logger.log(logRecord);
	}

	/**
	 * Build and return an action for opening the development-time
	 * only Help Topic ID Window.
	 */
	public static FrameworkAction buildHelpTopicIDWindowAction(WorkbenchContext context) {
		return new HelpTopicIDWindowAction(context);
	}

	/**
	 * Build and return a preferences node for the preferences dialog.
	 */
	public static PreferencesNode buildHelpPreferencesNode(PreferencesContext context) {
		PreferencesContext helpContext = (PreferencesContext) context.buildRedirectedPreferencesContext(HELP_PREFERENCES_NODE);
		return new HelpPreferencesNode(helpContext);
	}

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private HelpFacade() {
		super();
		throw new UnsupportedOperationException();
	}


	// ********** internal exceptions **********

	/** throw when we have problems with building a "local" help book */
	private static class LocalHelpBookWarning extends RuntimeException {
		LocalHelpBookWarning() {
			super();
		}
	}

}
