/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;



final class GenerateDescriptorsFromSelectedTablesAction extends AbstractFrameworkAction {

    GenerateDescriptorsFromSelectedTablesAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        initializeTextAndMnemonic("SELECTED_TABLES");
    }

    protected void execute() {
        RelationalProjectNode projectNode = (RelationalProjectNode) selectedNodes()[0].getProjectRoot();
        DescriptorGenerationCoordinator coordinator = new DescriptorGenerationCoordinator(getWorkbenchContext());
        coordinator.generateClassDescriptorsForSelectedTables(projectNode, CollectionTools.collection(tables(CollectionTools.iterator(selectedNodes()))));
    }

    private Iterator tables(Iterator selectedTableNodes) {
        return new TransformationIterator(selectedTableNodes) {
            protected Object transform(Object next) {
                return ((TableNode) next).getTable();
            }

        };
    }

}
