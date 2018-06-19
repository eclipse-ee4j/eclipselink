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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;



final class GenerateTablesFromSelectedDescriptorsAction extends AbstractGenerateTablesFromDescriptorsAction {

    GenerateTablesFromSelectedDescriptorsAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        initializeTextAndMnemonic("SELECTED_DESCRIPTORS_MENU_ITEM");
    //    initializeIcon("table.remove");
    }

    protected void execute() {
        generateTablesFromDescriptors(CollectionTools.collection(selectedDescriptors()));
    }

    private Iterator selectedDescriptors() {
        return new TransformationIterator(CollectionTools.iterator(selectedNodes())) {
            protected Object transform(Object next) {
                return ((DescriptorNode) next).getDescriptor();
            }
        };
    }
}
