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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;


public final class ExportDeploymentXmlAction extends AbstractEnablableFrameworkAction {

    ExportDeploymentXmlAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        super.initialize();
        initializeText("EXPORT_DEPLOYMENT_XML_ACTION");
        initializeMnemonic("EXPORT_DEPLOYMENT_XML_ACTION");
        initializeAccelerator("EXPORT_DEPLOYMENT_XML_ACTION.accelerator");
        initializeToolTipText("EXPORT_DEPLOYMENT_XML_ACTION.toolTipText");
        initializeIcon("GENERATE_XML");
    }

    protected void execute() {
        ProjectDeploymentXmlGenerationCoordinator coordinator = new ProjectDeploymentXmlGenerationCoordinator(getWorkbenchContext());
        ApplicationNode[] projectNodes = selectedProjectNodes();
        for (int i = 0; i < projectNodes.length; i++) {
            coordinator.exportProjectDeploymentXml((MWProject) projectNodes[i].getValue());
        }
    }

    protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
        return selectedProjectNodes().length > 0;
    }
}
