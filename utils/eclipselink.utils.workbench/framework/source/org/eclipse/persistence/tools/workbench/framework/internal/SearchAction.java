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
 * Action for opening help to search tab
 */
final class SearchAction extends AbstractFrameworkAction {
	SearchAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		initializeTextAndMnemonic("HELP_SEARCH");
//		initializeIcon("search");
	}

	protected void execute() {
		helpManager().showSearch();
	}
}
