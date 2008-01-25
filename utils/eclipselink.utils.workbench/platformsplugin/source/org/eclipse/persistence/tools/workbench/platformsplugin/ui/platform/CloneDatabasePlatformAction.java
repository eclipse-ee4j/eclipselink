/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;


/**
 * clone the selected nodes
 */
final class CloneDatabasePlatformAction extends AbstractFrameworkAction {

	public CloneDatabasePlatformAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("CLONE_DATABASE_PLATFORM");
		// no accelerator
		this.initializeIcon("CLONE_DATABASE_PLATFORM");
		this.initializeToolTipText("CLONE_DATABASE_PLATFORM.TOOL_TIP");
	}

	protected void execute(ApplicationNode selectedNode) {
		DatabasePlatform platform = ((DatabasePlatformNode) selectedNode).getDatabasePlatform();
		platform.getRepository().clone(platform);
	}

}
