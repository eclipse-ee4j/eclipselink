/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * close the selected nodes;
 * built anew every time a node is selected, so it's always enabled;
 * this action is part of the SelectionMenu, while WorkbenchCloseAction is
 * part of the FileMenu and MainToolBar
 */
final class CloseAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;


	CloseAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("file.close");
		this.initializeIcon("file.close");
		this.initializeToolTipText("file.close.toolTipText");
		this.initializeAccelerator("file.close.ACCELERATOR");
	}

	protected void execute() {
		this.nodeManager.close(this.selectedProjectNodes(), this.getWorkbenchContext());
	}

}