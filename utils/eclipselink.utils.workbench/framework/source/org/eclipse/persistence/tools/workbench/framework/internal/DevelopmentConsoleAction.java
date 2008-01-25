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
 * development-only action that displays the development console
 */
final class DevelopmentConsoleAction
	extends AbstractFrameworkAction
{
	/** There is only one console per application. */
	private FrameworkApplication application;

	public DevelopmentConsoleAction(WorkbenchContext context, FrameworkApplication application) {
		super(context);
		this.application = application;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction#initialize()
	 */
	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("DEVELOPMENT_CONSOLE");
	}

	/**
	 * ignore the selected nodes
	 */
	protected void execute() {
		application.openDevelopmentConsole();
	}

}
