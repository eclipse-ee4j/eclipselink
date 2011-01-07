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

import java.awt.Component;
import java.util.Collection;
import java.util.Map;

import javax.swing.JMenuItem;

/**
 * This interface provides a way for UI code to display Help and/or install
 * Context-Sensitive Help (CSH) for a set of components.
 */
public interface HelpManager {

	/**
	 * Show the default Help window.
	 */
	void showHelp();
	
	/**
	 * Display Help for the specified Topic ID.
	 */
	void showTopic(String topicID);

	/**
	 * Display Help for the specified component, if it has been registered
	 * for Context-Sensitive Help.
	 */
	void showTopic(Component component);

	/**
	 * Associate Help Topic IDs with the specified UI components.
	 * If a component, or any of its children, triggers Context-Sensitive
	 * Help (either via pressing F1 or a pop-up menu) the appropriate
	 * Help Topic will be displayed in the Help window. The map should
	 * contain a set of Help Topic IDs, keyed by their associated UI
	 * components.
	 */
	void addTopicIDs(Map componentsToTopicIDs);

	/**
	 * Associate a Help Topic ID with the specified UI component.
	 * If the component, or any of its children, triggers Context-Sensitive
	 * Help (either via pressing F1 or a pop-up menu) the appropriate
	 * Help Topic will be displayed in the Help window.
	 */
	void addTopicID(Component component, String topicID);

	/**
	 * Unregistered the specified UI components from Context-Sensitive Help.
	 */
	void removeTopicIDs(Map componentsToTopicIDs);

	/**
	 * Unregistered the specified UI components from Context-Sensitive Help.
	 */
	void removeTopicIDs(Collection components);

	/**
	 * Unregistered the specified UI component from Context-Sensitive Help.
	 */
	void removeTopicID(Component component);

    /**
     * The helpManager automatically has a help menu item in the right click
     * popup menu.  This allows components to add menu items to that menu.
     */
    void addItemsToPopupMenuForComponent(JMenuItem[] menuItems, Component component);
            
	/**
	 * Shutdown Context-Sensitive Help. No further methods should called on
	 * the help manager after this method is called.
	 */
	void shutDown();

}
