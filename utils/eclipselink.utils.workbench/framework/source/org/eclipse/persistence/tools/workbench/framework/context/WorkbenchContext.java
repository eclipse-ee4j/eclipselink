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
package org.eclipse.persistence.tools.workbench.framework.context;

import java.awt.Component;
import java.awt.Window;

import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.resources.IconResourceFileNameMap;


/**
 * Add workbench window-specific context to the application context.
 */
public interface WorkbenchContext {

	/** Return the "application" context, which includes all the application-wide state */
	ApplicationContext getApplicationContext();

	/** Return the current workbench window - useful for opening dialogs. */
	Window getCurrentWindow();

	/**
	 * Return a navigator selection model that can be used to
	 * manipulate the selections and expansions in the workbench
	 * window's navigator view.
	 */
	NavigatorSelectionModel getNavigatorSelectionModel();

	/** Return the workbench-specific file actions needed by the plug-ins and nodes. */
	ActionRepository getActionRepository();
    
    /** Return the propertiees page currently selected in the EditorView **/
    Component getPropertiesPage();

	/** Build and return a context with expanded resources. */
	WorkbenchContext buildExpandedApplicationContextWorkbenchContext(ApplicationContext appContext);
	
	/** Build and return a context with expanded resources. */
	WorkbenchContext buildExpandedResourceRepositoryContext(Class resourceBundleClass);

	/** Build and return a context with expanded resources. */
	WorkbenchContext buildExpandedResourceRepositoryContext(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap);

	/** Build and return a context with expanded resources. */
	WorkbenchContext buildExpandedResourceRepositoryContext(IconResourceFileNameMap iconResourceFileNameMap);

}
