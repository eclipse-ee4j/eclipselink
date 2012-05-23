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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;


public final class ExportJavaSourceAction extends AbstractFrameworkAction {

	ExportJavaSourceAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		initializeText("EXPORT_PROJECT_JAVA_SOURCE_ACTION");
		initializeMnemonic("EXPORT_PROJECT_JAVA_SOURCE_ACTION");
		initializeAccelerator("EXPORT_PROJECT_JAVA_SOURCE_ACTION.accelerator");
		initializeToolTipText("EXPORT_PROJECT_JAVA_SOURCE_ACTION.toolTipText");
		initializeIcon("GENERATE_JAVA");
		
		updateEnabledState();
	}
		
	protected void execute() {
		ProjectSourceGenerationCoordinator coordinator = new ProjectSourceGenerationCoordinator(getWorkbenchContext());
		ApplicationNode[] projectNodes = selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
			coordinator.exportProjectSource((MWProject) projectNodes[i].getValue());
		}
	}

	private boolean shouldBeEnabled(ApplicationNode selectedNode) {
		//TODO there has to be another way to do this
		if( selectedNode instanceof MappingsApplicationNode) {
			return ((ProjectNode) selectedNode.getProjectRoot()).supportsExportProjectJavaSource();
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
