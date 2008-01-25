/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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