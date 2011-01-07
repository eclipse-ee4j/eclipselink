/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
