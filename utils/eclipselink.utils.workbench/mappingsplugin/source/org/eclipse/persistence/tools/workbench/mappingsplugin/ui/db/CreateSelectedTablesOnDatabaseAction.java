/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;



class CreateSelectedTablesOnDatabaseAction extends AbstractCreateTablesOnDatabaseAction {

	CreateSelectedTablesOnDatabaseAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("SELECTED_TABLES_LABEL");
	}

	protected Collection buildTables() {
		return this.selectedMWTables();
	}

}
