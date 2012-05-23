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

import java.io.File;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

public final class ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction extends AbstractEnablableFrameworkAction {

	ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		initializeTextAndMnemonic("EXPORT_DEPLOYMENT_XML_AND_INITIALIZE_RUNTIME_DESCRIPTORS_ACTION");
		initializeToolTipText("EXPORT_DEPLOYMENT_XML_AND_INITIALIZE_RUNTIME_DESCRIPTORS_ACTION.toolTipText");
		initializeIcon("GENERATE_XML");
	}
		
	protected void execute() {
		ProjectDeploymentXmlGenerationCoordinator coordinator = new ProjectDeploymentXmlGenerationCoordinator(getWorkbenchContext());	
		ApplicationNode[] projectNodes = selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
            MWProject mwProject = (MWProject) projectNodes[i].getValue();
			if (coordinator.exportProjectDeploymentXml(mwProject)) {
	            File deploymentXmlFile = mwProject.deploymentXMLFile();
	            Project project = XMLProjectReader.read(deploymentXmlFile.getAbsolutePath(), buildClassLoader(mwProject));
	            DatabaseSessionImpl session = (DatabaseSessionImpl) project.createDatabaseSession();
	            session.dontLogMessages();
	            //hard to login since I don't know how to define a url for eis connections
	            //This will do, initialization is what we want to test anyway.
	            session.initializeDescriptors();
	     
	            JOptionPane.showMessageDialog(currentWindow(), resourceRepository().getString("EXPORT_DEPLOYMENT_XML_AND_INITIALIZE_RUNTIME_DESCRIPTORS_STATUS_BAR"));      
			}
		}
	}
    
    private ClassLoader buildClassLoader(MWProject project) {
		Classpath cp = new Classpath(CollectionTools.list(project.getRepository().fullyQualifiedClasspathEntries()));
        return new URLClassLoader(cp.urls(), this.getClass().getClassLoader());
    }
    
    protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return selectedProjectNodes().length > 0;
	}
}
