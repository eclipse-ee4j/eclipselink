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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class InterfaceDescriptorNode
	extends DescriptorNode
{

	// ********** constructors/initialization **********

	public InterfaceDescriptorNode(MWInterfaceDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}



	// ********** DescriptorNode implementation **********

	protected boolean supportsAdvancedProperties() {
		return false;
	}
	
	protected boolean supportsDescriptorMorphing() {
		return false;
	}

	// ********** ApplicationNode implementation **********

	public String helpTopicID() {
		return "descriptor.interface";
	}

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context)
	{
		GroupContainerDescription desc =  super.buildMenuDescription(context);
		context = buildLocalWorkbenchContext(context);
		
		MenuGroupDescription classItems = new MenuGroupDescription();
		classItems.add(getMappingsPlugin().getRefreshClassesAction(context));
		classItems.add(getMappingsPlugin().getAddOrRefreshClassesAction(context));
		classItems.add(getMappingsPlugin().getCreateNewClassAction(context));
		desc.add(classItems);
		
		MenuGroupDescription descItems = new MenuGroupDescription();
		descItems.add(getRemoveDescriptorAction(context));
		descItems.add(getRenameDescriptorAction(context));
		descItems.add(getMoveDescriptorAction(context));
		desc.add(descItems);
		
		desc.add(buildOracleHelpMenuGroup(context));
		
		return desc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		return new ToolBarDescription();
	}
	
	protected String accessibleNameKey() {
		return "ACCESSIBLE_INTERFACE_DESCRIPTOR_NODE";
	}

	public String buildIconKey() {
		return "descriptor.interface";
	}


	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return InterfaceDescriptorPropertiesPage.class;
	}

	
	// ********** DescriptorNode overrides **********
	
	public boolean hasEjbPolicy() {
		return false;
	}
	
}
