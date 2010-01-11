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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


/**
 * The ApplicationNode is the main controller for the UI.
 * Each plugin defines these as wrappers for every NodeModel object
 * appearing in the tree. The ApplicationNodes will be displayed in the
 * tree in the Navigator view.
 *
 * @see AbstractApplicationNode
 */
public interface ApplicationNode
	extends TreeNodeValueModel, Displayable, EditorNode, ApplicationProblemContainer, AccessibleNode
{

	/**
	 * Return the application node's node.
	 */
// this should be allowed in jdk 1.5:
//	Node (or NodeModel?) getValue();

	/**
	 * Return the node's application context.
	 */
	ApplicationContext getApplicationContext();

	/**
	 * Return the plug-in that created the node.
	 */
	Plugin getPlugin();

	/**
	 * Return the plug-in-specific root node. This is typically
	 * the node created by the plug-in when it reads in a
	 * "project" file. This is NOT the root of the navigator tree.
	 */
	ApplicationNode getProjectRoot();

	/**
	 * Return whether the node or one of its children have been modified.
	 * If it is dirty, the tree cell renderer will place an asterisk '*' at the
	 * front of the node's display string.
	 */
	boolean isDirty();
		String DIRTY_PROPERTY = "dirty";

	/**
	 * Called when the user chooses to save this project.
	 * The mostRecentSaveDirectory is persisted as a preference by the framework
	 * The node may use this as the default directory in a JFileChooser
	 */
	boolean save(File mostRecentSaveDirectory, WorkbenchContext workbenchContext);

	/**
	 * Called when the user chooses to perform a saveAs on this project.
	 * Node is responsible for prompting the user for a save location.
	 * The mostRecentSaveDirectory is persisted as a preference by the framework
	 * The node may use this as the default directory in a JFileChooser
	 */
	boolean saveAs(File mostRecentSaveDirectory, WorkbenchContext workbenchContext);

	/**
	 * Return the file holding the project.
	 * Return null if the project has not been saved.
	 */
	File saveFile();

	/**
	 * Return the Topic ID used by the HelpManager for this node.
	 * Pressing F1 on the tree will show the Help Topic associated with
	 * this Topic ID.
	 */
	String helpTopicID();

	/**
	 * Return a GroupContainerDescription that represents how the
	 * selection menu for the node should be built.
	 * Typically this is a RootMenuDescription.
	 */
	GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext);

	/**
	 * Return a GroupContainerDescription that represents how the
	 * selection tool bar for the node should be built.
	 * Typically this is a ToolBarDescription.
	 */
	GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext);

	/**
	 * Return the descendant application node with the specified value.
	 * Return null if there is no such application node.
	 */
	ApplicationNode descendantNodeForValue(Node node);
	
	/**
	 * Typically this is delegated to the node's value; but some nodes
	 * do not have values so it is handled directly by the node or ignored.
	 */
	void addValuePropertyChangeListener(String propertyName, PropertyChangeListener listener);
	
	/**
	 * Typically this is delegated to the node's value; but some nodes
	 * do not have values so it is handled directly by the node or ignored.
	 */
	void removeValuePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}
