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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import javax.swing.JMenuItem;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class EisRootDescriptorNode extends EisDescriptorNode {


	// ********** constructors/initialization **********
	
	public EisRootDescriptorNode(MWRootEisDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}
			
	public String helpTopicID() {
		return "descriptor.eis.root";
	}

	public String buildIconKey() {
		return "descriptor.eis.root";
	}
	
	protected JMenuItem buildAdvancedPropertiesMenuItem(WorkbenchContext workbenchContext)
	{
		JMenuItem menuItem = new JMenuItem();
		menuItem.setAction(getAdvancedPolicyAction(buildLocalWorkbenchContext(workbenchContext)));
		
		return menuItem;
	}

	// ********** DescriptorNode implementation **********

	protected String accessibleNameKey() {
		return "ACCESSIBLE_EIS_ROOT_DESCRIPTOR_NODE";
	}

	// ********** DescriptorNode overrides *************

	public boolean supportsInterfaceAliasPolicy() {
		return true;	
	}

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return EisRootDescriptorTabbedPropertiesPage.class;
	}

	
	public boolean isRootDescriptor() {
		return true;
	}
	
	public boolean supportsTransactionalDescriptorProperties() {
		return true;
	}
}
