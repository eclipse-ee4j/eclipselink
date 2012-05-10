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
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * loop through the selected "project" nodes and tell them each to save-as;
 * built anew every time a node is selected, so it's always enabled;
 * this action is part of the SelectionMenu, while WorkbenchSaveAsAction is
 * part of the FileMenu and MainToolBar
 */
final class SaveAsAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;


	SaveAsAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("file.saveAs");
		this.initializeIcon("file.saveAs");
		this.initializeToolTipText("file.saveAs.toolTipText");
		this.initializeAccelerator("file.saveAs.ACCELERATOR");
	}

	protected void execute() {
		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = projectNodes.length; i-- > 0; ) {
			this.nodeManager.saveAs(projectNodes[i], getWorkbenchContext());
		}
	}

}
