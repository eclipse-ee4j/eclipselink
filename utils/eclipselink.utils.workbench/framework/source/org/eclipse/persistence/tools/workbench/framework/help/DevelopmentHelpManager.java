/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.help;

import java.awt.Component;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


/**
 * This help manager will display the current Help Topic ID
 * in a little window and indicate whether it is valid.
 */
class DevelopmentHelpManager extends DefaultHelpManager {

	/**
	 * A value model that holds the last help topic ID
	 * and is shared with the help topic ID window.
	 */
	private PropertyValueModel helpTopicIDHolder;

	/**
	 * A value model that holds a flag indicating whether
	 * the last help topic ID was valid and is shared with
	 * the help topic ID window.
	 */
	private PropertyValueModel helpTopicIDIsValidHolder;

	/** A development-time-only window used for debugging Help topics. */
	private JFrame helpTopicIDWindow;


	// ********** constructors **********

	/**
	 * construct a "development" help manager that uses the specified OHJ help and book
	 */
	DevelopmentHelpManager(ResourceRepository resourceRepository, Preferences preferences) {
		super(resourceRepository, preferences);
	}

	// ********** initialization **********

	/**
	 * @see DefaultHelpManager#initialize()
	 */
	protected void initialize() {
		super.initialize();
		this.helpTopicIDHolder = new SimplePropertyValueModel();
		this.helpTopicIDIsValidHolder = new SimplePropertyValueModel(Boolean.TRUE);
		this.helpTopicIDWindow = this.buildHelpTopicIDWindow();
	}

	private JFrame buildHelpTopicIDWindow() {
		JFrame window = new JFrame("Help Topic ID");
		window.getContentPane().add(this.buildHelpTopicIDPanel(), "Center");
		window.setLocation(100, 100);
		window.setSize(300, 100);
		return window;
	}

	/**
	 * Pass the shared models.
	 */
	private Component buildHelpTopicIDPanel() {
		return new HelpTopicIDPanel(this.helpTopicIDHolder, this.helpTopicIDIsValidHolder);
	}


	// ********** overrides **********

	/**
	 * extend to set the topic ID and whether it is valid
	 * @see DefaultHelpManager#showTopicInternal(String)
	 */
	protected void showTopicInternal(String topicID) {
		this.helpTopicIDHolder.setValue(topicID);
		super.showTopicInternal(topicID);
		this.helpTopicIDIsValidHolder.setValue(Boolean.TRUE);
	}


	// ********** behavior **********

	/**
	 * Open a little window that will display the current Help
	 * Topic ID and indicate whether the topic ID is valid.
	 * This method should only be called in "development" mode.
	 */
	void openHelpTopicIDWindow() {
		this.helpTopicIDWindow.setVisible(true);
	}

}
