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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session;

import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.SCApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.DeleteSessionAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.RenameSessionAction;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * Abstract class for SC Nodes that wrap a SessionAdapter.
 */
public abstract class SessionNode extends SCApplicationNode {

	/**
	 * Constructor for SessionNode
	 */
	public SessionNode( SCAdapter scAdapter, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {

		super( scAdapter, parent, plugin, context);
	}

	// **************** factory methods ****************************************

	protected List buildDisplayStringPropertyNamesList() {
		
		List displayStrings = super.buildDisplayStringPropertyNamesList();
		displayStrings.add( SessionAdapter.NAME_PROPERTY);
		return displayStrings;
	}
	
	protected FrameworkAction buildRenameNodeAction(WorkbenchContext workbenchContext) {
		
		return new RenameSessionAction(workbenchContext);
	}

	protected FrameworkAction buildDeleteNodeAction(WorkbenchContext workbenchContext) {

		return new DeleteSessionAction(workbenchContext);
	}

	SessionAdapter session() {
		
		return ( SessionAdapter)this.getValue();
	}

}
