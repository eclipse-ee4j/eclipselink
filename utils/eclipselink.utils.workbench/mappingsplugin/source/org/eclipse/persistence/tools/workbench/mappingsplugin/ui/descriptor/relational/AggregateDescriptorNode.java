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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class AggregateDescriptorNode extends RelationalClassDescriptorNode {


	// ********** constructors/initialization **********
	
	public AggregateDescriptorNode(MWAggregateDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}
	

	// ********** ApplicationNode implementation **********

	public String helpTopicID() {
		return "descriptor.aggregate";
	}

	public String buildIconKey() {
		return "descriptor.aggregate";
	}

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return AggregateDescriptorTabbedPropertiesPage.class;
	}


	// ********** DescriptorNode implementation **********

	protected String accessibleNameKey() {
		return "ACCESSIBLE_AGGREGATE_DESCRIPTOR_NODE";
	}
	
	
	// ********** MWRelationalClassDescriptorNode overrides **********
	
	public boolean isAggregateDescriptor() {
		return true;
	}
    
    public void selectQueryKey(MWQueryKey queryKey, WorkbenchContext context) {
        ((AggregateDescriptorTabbedPropertiesPage) context.getPropertiesPage()).selectQueryKey(queryKey);   
	}

}
