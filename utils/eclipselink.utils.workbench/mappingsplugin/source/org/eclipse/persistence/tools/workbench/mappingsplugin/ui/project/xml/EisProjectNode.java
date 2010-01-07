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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml;

import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode.DescriptorNodeBuilder;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.EisCompositeDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.EisRootDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectTabbedPropertiesPage;


public final class EisProjectNode
	extends XmlProjectNode
{
	// **************** Constructors ******************************************
	
	public EisProjectNode(MWEisProject project, ApplicationContext context, MappingsPlugin plugin) {
		super(project, plugin, context);
	}
	    
	// ********** MWApplicationNode overrides **********
	
	protected Class propertiesPageClass() {
		return EisProjectTabbedPropertiesPage.class;
	}
	
	protected ProjectTabbedPropertiesPage buildProjectTabbedPanePropertiesPage(WorkbenchContext context) {
		return new EisProjectTabbedPropertiesPage(context);
	}
	
	
	// *********** AbstractApplicationNode implementation ***********
	
	protected String buildIconKey() {
		return "project.eis";
	}
	

	// *********** AbstractApplicationNode implementation ***********
	
	protected String accessibleNameKey() {
		return "ACCESSIBLE_EIS_PROJECT_NODE";
	}


	// *********** ApplicationNode implementation *********
	
	protected void addToMenuDescription(GroupContainerDescription menuDescription, WorkbenchContext context) {
		menuDescription.add(this.buildClassActionGroup(context));
		menuDescription.add(this.buildCloseDeleteActionGroup(context));
		menuDescription.add(this.buildSaveActionGroup(context));
		menuDescription.add(this.buildExportActionGroup(context));
		menuDescription.add(this.buildOracleHelpMenuGroup(context));
	}
	
	
	// *********** ProjectNode implementation ***********
	
	protected DescriptorNodeBuilder buildDescriptorNodeBuilder() {
		return new DescriptorPackageNode.DescriptorNodeBuilder() {
			public DescriptorNode buildDescriptorNode(MWDescriptor descriptor, DescriptorPackageNode descriptorPackageNode) {
				if (descriptor instanceof MWRootEisDescriptor) {
					return new EisRootDescriptorNode((MWRootEisDescriptor) descriptor, descriptorPackageNode);
				} else if (descriptor instanceof MWCompositeEisDescriptor) {
					return new EisCompositeDescriptorNode((MWCompositeEisDescriptor) descriptor, descriptorPackageNode);
				} else {
					throw new IllegalArgumentException(descriptor.toString());
				}
			}
		};
	}
}
