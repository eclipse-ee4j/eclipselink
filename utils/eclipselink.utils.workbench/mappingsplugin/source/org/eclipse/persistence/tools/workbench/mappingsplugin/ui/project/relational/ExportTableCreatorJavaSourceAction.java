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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;



public final class ExportTableCreatorJavaSourceAction
    extends AbstractFrameworkAction
{

    public ExportTableCreatorJavaSourceAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        initializeText("exportTableCreatorJavaSource");
        initializeMnemonic("exportTableCreatorJavaSource");
        initializeAccelerator("exportTableCreatorJavaSource.accelerator");
        initializeToolTipText("exportTableCreatorJavaSource.toolTipText");
        initializeIcon("GENERATE_JAVA");

        updateEnabledState();
    }

    protected void execute(ApplicationNode selectedNode) {
        TableCreatorSourceGenerationCoordinator coordinator = new TableCreatorSourceGenerationCoordinator(getWorkbenchContext());
        coordinator.exportTableCreatorSource((MWRelationalProject) selectedNode.getProjectRoot().getValue());
    }

    private boolean shouldBeEnabled(ApplicationNode selectedNode) {
        //TODO there has to be another way to do this
        if( selectedNode instanceof MappingsApplicationNode) {
            return ((ProjectNode) selectedNode.getProjectRoot()).supportsExportTableCreatorJavaSource();
        }
        return false;
    }

    private void updateEnabledState()
    {
        for (int index = 0; index < selectedNodes().length; index++)
        {
            if (!shouldBeEnabled(selectedNodes()[index])) {
                setEnabled(false);
                return;
            }
        }

        setEnabled(true);
    }
}
