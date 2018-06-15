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
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ChangeDescriptorTypeAction;


final class AggregateDescriptorAction
    extends ChangeDescriptorTypeAction
{
    AggregateDescriptorAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        initializeIcon("descriptor.aggregate");
        initializeText("MORPH_TO_AGGREGATE_DESCRIPTOR_ACTION.text");
        initializeToolTipText("MORPH_TO_AGGREGATE_DESCRIPTOR_ACTION.toolTipText");
    }

    protected MWDescriptor morphDescriptor(MWDescriptor descriptor) {
        return  ((MWRelationalDescriptor) descriptor).asMWAggregateDescriptor();
    }

    protected boolean shouldBeSelected(ApplicationNode selectedNode) {
        return ((RelationalClassDescriptorNode) selectedNode).isAggregateDescriptor();
    }
}
