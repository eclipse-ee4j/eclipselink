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
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * exit the application;
 * always enabled
 */
final class ExitAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;

	ExitAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		this.initializeTextAndMnemonic("EXIT_ACTION");
		this.initializeIcon("EXIT");
		this.initializeToolTipText("EXIT_ACTION.TOOL_TIP");
	}

	public void execute() {
		nodeManager.exit(this.getWorkbenchContext());
	}

}
