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
package org.eclipse.persistence.tools.workbench.framework.help;

import java.awt.Component;
import java.util.Collection;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 *  Used when the help system has difficulty initializing - does nothing.
 */
final class NullHelpManager
	implements InternalHelpManager
{
	/** labels, messages, etc. */
	private ResourceRepository resourceRepository;


	/**
	 * this is hacked in to by tests that just need a dummy help manager
	 */
	private NullHelpManager() {
		super();
	}

	NullHelpManager(ResourceRepository resourceRepository) {
		this();
		this.resourceRepository = resourceRepository;
	}


	// ********** LocalHelpManager implementation **********

	/**
	 * @see InternalHelpManager#setLocalHelpFailed(boolean)
	 */
	public void setLocalHelpFailed(boolean localHelpFailed) {
		// ignore - if we have a "null" help manager, we obviously had bigger
		// problems than just a failure of "local" help
	}

	/**
	 * The application has completed its launch, notify the user
	 * that Help has been disabled.
	 * @see LocalHelpManager#launchComplete()
	 */
	public void launchComplete() {
		JOptionPane.showMessageDialog(null, this.resourceRepository.getString("HELP_INITIALIZATION_ERROR", StringTools.CR));
	}


	// ********** LocalHelpManager implementation **********

	/**
	 * @see HelpManager#showHelp()
	 */
	public void showHelp() {
		// do nothing
	}
	
	/**
	 * @see HelpManager#showTopic(String)
	 */
	public void showTopic(String topicID) {
		// do nothing
	}

	/**
	 * @see HelpManager#showTopic(java.awt.Component)
	 */
	public void showTopic(Component component) {
		// do nothing
	}

	/**
	 * @see HelpManager#addTopicIDs(java.util.Map)
	 */
	public void addTopicIDs(Map componentsToTopicIDs) {
		// do nothing
	}

	/**
	 * @see HelpManager#addTopicID(java.awt.Component, String)
	 */
	public void addTopicID(Component component, String topicID) {
		// do nothing
	}

	/**
	 * @see HelpManager#removeTopicIDs(java.util.Map)
	 */
	public void removeTopicIDs(Map componentsToTopicIDs) {
		// do nothing
	}

	/**
	 * @see HelpManager#removeTopicIDs(java.util.Collection)
	 */
	public void removeTopicIDs(Collection components) {
		// do nothing
	}

	/**
	 * @see HelpManager#removeTopicID(java.awt.Component)
	 */
	public void removeTopicID(Component component) {
		// do nothing
	}

    /**
     * @see HelpManager#addItemsToPopupMenuForComponent(JMenuItem[], Component)
     */
    public void addItemsToPopupMenuForComponent(JMenuItem[] menuItems, Component component) {
        // do nothing        
    }
	/**
	 * @see HelpManager#shutDown()
	 */
	public void shutDown() {
		// do nothing
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "NullHelpManager";
	}

}
