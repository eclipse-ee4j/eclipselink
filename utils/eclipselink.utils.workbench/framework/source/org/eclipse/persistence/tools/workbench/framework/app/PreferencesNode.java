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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.util.prefs.Preferences;

import javax.swing.tree.MutableTreeNode;

/**
 * Define an interface for preferences nodes that allows them
 * to play well with the tree model, the editor view, and the
 * cell renderer.
 */
public interface PreferencesNode extends EditorNode, MutableTreeNode {

	/**
	 * Return the preferences node associated with this node.
	 */
	Preferences getPreferences();

	/**
	 * Return the string to be displayed in the navigator tree
	 * and at the top of the editor view.
	 */
	String displayString();

	/**
	 * Return the topic ID used by the HelpManager for this node.
	 * Pressing F1 on the tree will show the help associated with 
	 * the topic ID.
	 */
	String helpTopicId();

}
