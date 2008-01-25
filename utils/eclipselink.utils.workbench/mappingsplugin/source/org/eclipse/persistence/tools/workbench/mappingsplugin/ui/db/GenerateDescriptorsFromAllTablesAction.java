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

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;



final class GenerateDescriptorsFromAllTablesAction extends AbstractFrameworkAction {

	GenerateDescriptorsFromAllTablesAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		initializeTextAndMnemonic("ALL_TABLES");
	}
	
	protected void execute() {
		for (int i = 0; i < selectedProjectNodes().length; i++) {
			RelationalProjectNode projectNode = (RelationalProjectNode) selectedProjectNodes()[i];
			DescriptorGenerationCoordinator coordinator = new DescriptorGenerationCoordinator(getWorkbenchContext());
			coordinator.generateClassDescriptorsForAllTables(projectNode);
		}
	}
}
