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
 * show Help for the Problems View
 */
final class ProblemsViewHelpAction extends AbstractFrameworkAction {

	public ProblemsViewHelpAction(WorkbenchContext workbenchContext) {
		super(workbenchContext);
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("CSH_HELP");
	}

	protected void execute() {
		this.helpManager().showTopic("problemsPane");
	}

}
