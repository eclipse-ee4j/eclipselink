/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen.ModelSourceGenerationCoordinator;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;


public class ExportSpecificDescriptorModelJavaSourceAction extends AbstractFrameworkAction {

	ExportSpecificDescriptorModelJavaSourceAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("EXPORT_MODEL_JAVA_SOURCE_DESCRIPTOR");
		this.initializeAccelerator("EXPORT_MODEL_JAVA_SOURCE_DESCRIPTOR.accelerator");
		this.initializeToolTipText("EXPORT_MODEL_JAVA_SOURCE_DESCRIPTOR.toolTipText");
		this.initializeIcon("GENERATE_JAVA");
	}
	
	protected void execute() {
		ApplicationNode[] projectNodes = selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
			ModelSourceGenerationCoordinator coordinator = new ModelSourceGenerationCoordinator(this.getWorkbenchContext());
			ProjectNode projectNode = (ProjectNode) projectNodes[i];
			MWProject project = projectNode.getProject();
			coordinator.exportModelJavaSource(project, this.selectedDescriptorsFor(projectNode));
		}
	}	

	private Collection selectedDescriptorsFor(ProjectNode projectNode) {
		Collection descriptors = new HashSet();
		ApplicationNode[] selectedNodes = this.selectedNodes();
		for (int i = 0; i < selectedNodes.length; i++) {
			MappingsApplicationNode node = (MappingsApplicationNode) selectedNodes[i];
			if (node.getProjectNode() == projectNode) {
				node.addDescriptorsTo(descriptors);
			}
		}
		
		return descriptors;
	}
	
}
