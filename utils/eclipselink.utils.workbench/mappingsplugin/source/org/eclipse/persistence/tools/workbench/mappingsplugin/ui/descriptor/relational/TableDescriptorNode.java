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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class TableDescriptorNode extends RelationalClassDescriptorNode {
	
	// ********** constructors/initialization **********
	
	public TableDescriptorNode(MWTableDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}
		

	
	// ********** ApplicationNode implementation **********

	public String helpTopicID() {
		return "descriptor.class";
	}
		
	public String buildIconKey() {
		return	"descriptor.class";
	}
	
	MWTableDescriptor getTableDescriptor() {
		return (MWTableDescriptor) getMappingDescriptor();
	}

	// ********** DescriptorNode implementation **********

	protected String accessibleNameKey() {
		return "ACCESSIBLE_TABLE_DESCRIPTOR_NODE";
	}
	
	// ********** DescriptorNode overrides *************

	public boolean supportsInterfaceAliasPolicy() {
		return true;	
	}

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return TableDescriptorTabbedPropertiesPage.class;
	}

	
	// ********** MWRelationalClassDescriptorNode overrides **********
	
	public boolean isTableDescriptor() {
		return true;
	}
	
	public boolean supportsTransactionalDescriptorProperties() {
		return true;
	}
    
    public void selectQueryKey(MWQueryKey queryKey, WorkbenchContext context) {
        ((TableDescriptorTabbedPropertiesPage) context.getPropertiesPage()).selectQueryKey(queryKey);   
}
}
