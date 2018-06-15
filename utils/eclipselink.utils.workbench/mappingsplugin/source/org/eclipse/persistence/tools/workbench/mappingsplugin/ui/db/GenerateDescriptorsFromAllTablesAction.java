/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
