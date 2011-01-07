/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedBoolean;


/**
 * "Data" object for holding configuration settings for building
 * a help manager.
 */
public class HelpManagerConfig {
	private Preferences preferences;
	private ResourceRepository resourceRepository;
	private Logger logger;
	private SynchronizedBoolean launchCompleteFlag;
	private boolean developmentMode;
	private boolean forceStupidWelcomeScreen;

	/**
	 * Return the preferences node under
	 * which the Help preferences are stored.
	 */
	public Preferences getPreferences() {
		return this.preferences;
	}

	/**
	 * Set the preferences node under
	 * which the Help preferences are stored.
	 */
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * Return the resource repository used to supply labels etc.
	 */
	public ResourceRepository getResourceRepository() {
		return this.resourceRepository;
	}

	/**
	 * Set the resource repository used to supply labels etc.
	 */
	public void setResourceRepository(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	/**
	 * Return the logger used to record any set-up problems.
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Set the logger used to record any set-up problems.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Return the flag that is set when the application launch is complete.
	 */
	public SynchronizedBoolean getLaunchCompleteFlag() {
		return this.launchCompleteFlag;
	}

	/**
	 * Set the flag that is set when the application launch is complete.
	 */
	public void setLaunchCompleteFlag(SynchronizedBoolean launchCompleteFlag) {
		this.launchCompleteFlag = launchCompleteFlag;
	}

	/**
	 * Return whether we are executing in "development mode".
	 */
	public boolean isDevelopmentMode() {
		return this.developmentMode;
	}

	/**
	 * Set whether we are executing in "development mode".
	 */
	public void setDevelopmentMode(boolean developmentMode) {
		this.developmentMode = developmentMode;
	}

	/**
	 * Return whether we are forcing the help manager to
	 * display the stupid Welcome Screen.
	 */
	public boolean forceStupidWelcomeScreen() {
		return this.forceStupidWelcomeScreen;
	}

	/**
	 * Force the help manager to display the stupid Welcome Screen.
	 */
	public void setForceStupidWelcomeScreen(boolean forceStupidWelcomeScreen) {
		this.forceStupidWelcomeScreen = forceStupidWelcomeScreen;
	}

}
