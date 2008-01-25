/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class EisCompositeDescriptorNode extends EisDescriptorNode {


	// ********** constructors/initialization **********
	
	public EisCompositeDescriptorNode(MWCompositeEisDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}
			
	
	// ********** ApplicationNode implementation **********

	public String helpTopicID() {
		return "descriptor.eis.composite";
	}
	
	public String buildIconKey() {
		return "descriptor.eis.composite";
	}

	// ********** DescriptorNode implementation **********

	protected String accessibleNameKey() {
		return "ACCESSIBLE_EIS_COMPOSITE_DESCRIPTOR_NODE";
	}

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return EisCompositeDescriptorTabbedPropertiesPage.class;
	}


	// ********** DescriptorNode overrides **********
	
	public boolean isCompositeDescriptor() {
		return true;
	}
}
