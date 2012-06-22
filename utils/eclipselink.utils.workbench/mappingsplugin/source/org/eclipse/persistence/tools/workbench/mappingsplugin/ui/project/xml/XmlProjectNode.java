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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml;

import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.UiDescriptorXmlBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.XmlDescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.UiMappingXmlBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema.XmlSchemaRepositoryNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;


public abstract class XmlProjectNode 
	extends ProjectNode 
{

	// *********** Constructors ***********

	protected XmlProjectNode(MWProject project, MappingsPlugin plugin, ApplicationContext context) {
		super(project, plugin, context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected ApplicationContext expandContext(ApplicationContext context) {
		return super.expandContext(context).
                buildExpandedResourceRepositoryContext(UiXmlBundle.class).
                buildExpandedResourceRepositoryContext(UiDescriptorXmlBundle.class).
                buildExpandedResourceRepositoryContext(UiMappingXmlBundle.class);
	}
	

	// ********** ProjectNode implementation **********

	public String getCannotAutomapDescriptorsStringKey() {
		return "XML_PROJECT_UNAUTOMAPPABLE";
	}

	
	// ************ ProjectNode implementation **********

	protected DescriptorPackageNode buildDescriptorPackageNodeFor(MWDescriptor descriptor) {
		return new XmlDescriptorPackageNode(descriptor.packageName(), this, this.getDescriptorNodeBuilder());
	}

	protected Child buildMetaDataRepositoryNode() {
		return new XmlSchemaRepositoryNode(this.getXmlProject().getSchemaRepository(), this);
	}


	// ********** MWApplicationNode overrides **********

	protected abstract ProjectTabbedPropertiesPage buildProjectTabbedPanePropertiesPage(WorkbenchContext context);


	// *********** ProjectNode implementation *********

	protected GroupContainerDescription buildExportMenuDescription(WorkbenchContext context) {

		MenuDescription menuDesc =
			new MenuDescription(
					this.resourceRepository().getString("EXPORT_MENU"), 
					this.resourceRepository().getString("EXPORT_MENU"),
					this.resourceRepository().getMnemonic("EXPORT_MENU"),
					this.resourceRepository().getIcon("file.export")
			);
		MenuGroupDescription groupDesc = new MenuGroupDescription();
        groupDesc.add(this.getExportDeploymentXmlAction(context));
        if (getMappingsPlugin().isDevelopmentModeIn(context)) {
            groupDesc.add(this.getExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(context));
        }		
        groupDesc.add(this.getModelJavaSourceAction(context));
		menuDesc.add(groupDesc);
		
		return menuDesc;
	}

	public boolean supportsExportProjectJavaSource() {
		return false;
	}
	
	public boolean supportsExportTableCreatorJavaSource() {
		return false;
	}


	// *********** Miscellaneous **************
	
	private MWXmlProject getXmlProject() {
		return (MWXmlProject) this.getProject();
	}

}
