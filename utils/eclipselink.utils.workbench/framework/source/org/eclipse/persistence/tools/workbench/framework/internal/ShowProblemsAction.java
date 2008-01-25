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

/**
 * toggle the display of problems in the workbench window;
 * always enabled
 */
final class ShowProblemsAction
	extends AbstractFrameworkAction
{
	/** we need access to the workbench window's internal api */
	private WorkbenchWindow workbenchWindow;


	public ShowProblemsAction(WorkbenchWindow workbenchWindow) {
		super(workbenchWindow.getContext());
		this.workbenchWindow = workbenchWindow;
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("window.showProblems");
		this.initializeIcon("window.showProblems");
		this.initializeToolTipText("window.showProblems.toolTipText");
		this.initializeAccelerator("window.showProblems.ACCELERATOR");
	}

	protected void execute() {
		this.workbenchWindow.toggleShowProblems();
	}

}
