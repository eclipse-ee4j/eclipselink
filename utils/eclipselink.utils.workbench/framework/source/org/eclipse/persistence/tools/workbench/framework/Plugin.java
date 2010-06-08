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
package org.eclipse.persistence.tools.workbench.framework;

import java.io.File;

import javax.swing.JMenuItem;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * Implementations of this interface are instantiated by the UI framework
 * upon start-up (via PluginFactory objects) and held for the duration
 * of the application's life.
 */
public interface Plugin {

	/**
	 * These menu items will be added to the workbench window's
	 * "File -> New" sub-menu.
	 * They allow the user to create a new "project" for the plug-in.
	 * A new set of menu items will be built for each workbench window.
	 * If the plug-in does not have any "new" menu items, it should return
	 * an empty array.
	 */
	JMenuItem[] buildNewMenuItems(WorkbenchContext context);

	/**
	 * When the user chooses to open a file, each of the plug-ins, in turn,
	 * will be asked to open the file. One of three things should happen:
	 * 1. the plug-in does NOT support the file type - it should throw an
	 * 	UnsupportedFileException
	 * 2. the plug-in DOES support the file type, but has problems opening
	 * 	the file - it should throw an OpenException
	 * 3. the plug-in has no problems opening the file - it should return
	 * 	an application node to be added to the tree as a root node
	 */
	ApplicationNode open(File file, WorkbenchContext context) throws UnsupportedFileException, OpenException;

	/**
	 * Return descriptions of the plug-in-specific components to appear
	 * in the "plug-in" tool bar (the top tool bar).
	 * The tool bar components will be enabled and disabled based on
	 * whether nodes associated with the plug-in are selected in the
	 * navigator.
	 */
	ComponentContainerDescription buildToolBarDescription(WorkbenchContext context);

	/**
	 * Return descriptions of the plug-in-specific components to appear
	 * in the "Workbench" menu (which appears to the right of the File Menu)
	 * when all the selected nodes in the navigator are associated with
	 * the plug-in.
	 */
	ComponentContainerDescription buildMenuDescription(WorkbenchContext context);

	/**
	 * Build the set of nodes to be displayed in the Preferences dialog
	 * that allow the plug-in's preferences to be configured by the user.
	 * If the plug-in does not have any preferences nodes, it should return
	 * an empty array.
	 */
	PreferencesNode[] buildPreferencesNodes(PreferencesContext context);

}
