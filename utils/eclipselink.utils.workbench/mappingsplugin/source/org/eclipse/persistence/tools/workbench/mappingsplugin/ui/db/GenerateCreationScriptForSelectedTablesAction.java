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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;



final class GenerateCreationScriptForSelectedTablesAction extends AbstractGenerateCreationScriptForTablesAction {

	GenerateCreationScriptForSelectedTablesAction(WorkbenchContext context) {
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
