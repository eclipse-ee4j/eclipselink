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
package org.eclipse.persistence.tools.workbench.framework.action;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Define the API required by the UI framework.
 */
public interface FrameworkAction extends Action {

	/**
	 * Return the text that names the action.
	 */
	String getText();

	/**
	 * Return the mnemonic that can be used to execute
	 * the action when it is in a menu.
	 */
	int getMnemonic();

	/**
	 * Return the keyboard shortcut that can be used to
	 * execute the action without using a menu or button.
	 */
	KeyStroke getAccelerator();

	/**
	 * Return the icon representing the action.
	 */
	Icon getIcon();

	/**
	 * Return a description of the action to be displayed
	 * when the user selects the menu item or button.
	 */
	String getToolTipText();
	
	/**
	 * Returns an identifier for this action that is used in 
	 * during comparison activities.  It is recommended that this
	 * identifier be unique so that equivalent actions compare correctly.
	 * However, certian situations may require that different actions
	 * compare as equivalent.
	 */
	String getClassification();
	
	/** 
	 * Use this to setup listeners or state for the action
	 * See SharedActionCacheManager to see how this method will be called
	 */
	void setUp();

	/** 
	 * Use this to remove listeners for the action 
	 * See SharedActionCacheManager to see how this method will be called
	 */
	void tearDown();

}
