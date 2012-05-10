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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.app.ActiveIconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.IconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.SimpleIconBuilder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWCompositeEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;


public class DescriptorCellRendererAdapter extends NoneSelectedCellRendererAdapter
{
	public DescriptorCellRendererAdapter(ResourceRepository repository) {
		super(repository);
	}

    protected Icon buildNonNullValueIcon(Object value) {
        MWDescriptor descriptor = (MWDescriptor) value;
		
		return new ActiveIconBuilder(buildIconBuilder(descriptor), descriptor.isActive()).buildIcon();
	}
	
	private IconBuilder buildIconBuilder(MWDescriptor descriptor) {
		return new SimpleIconBuilder(this.resourceRepository().getIcon(this.buildIconKey(descriptor)));
	}
	
	private String buildIconKey(MWDescriptor descriptor) {
		if (descriptor instanceof MWTableDescriptor) {
			return "descriptor.class";
		}
		else if (descriptor instanceof MWAggregateDescriptor) {
			return "descriptor.aggregate";
		}
		else if (descriptor instanceof MWRootEisDescriptor) {
			return "descriptor.eis.root";

		}
		else if (descriptor instanceof MWCompositeEisDescriptor) { 
				return "descriptor.eis.composite";
		}
		else if (descriptor instanceof MWOXDescriptor) {
			return "descriptor.ox";
		}
		else { //if (descriptor instanceof MWInterfaceDescriptor)
			return "descriptor.interface";
		}	
	}
		
	protected String buildNonNullValueText(Object value) {
		return ((MWDescriptor) value).displayStringWithPackage();
	}

	public String buildAccessibleName(Object value) {
		MWDescriptor descriptor = (MWDescriptor) value;
		StringBuffer sb = new StringBuffer();

		if (descriptor instanceof MWAggregateDescriptor) {
			sb.append(resourceRepository().getString("DESCRIPTOR_TYPE_AGGREGATE"));
		}
		else if (descriptor instanceof MWInterfaceDescriptor) {
			sb.append(resourceRepository().getString("DESCRIPTOR_TYPE_INTERFACE"));
		}
		else if (descriptor instanceof MWTableDescriptor) {
			sb.append(resourceRepository().getString("DESCRIPTOR_TYPE_RELATIONAL"));
		}
		else if (descriptor instanceof MWRootEisDescriptor) {
			sb.append(resourceRepository().getString("DESCRIPTOR_TYPE_ROOT_EIS"));
		}
		else if (descriptor instanceof MWCompositeEisDescriptor) {
			sb.append(resourceRepository().getString("DESCRIPTOR_TYPE_COMPOSITE_EIS"));
		}
		else if (descriptor instanceof MWOXDescriptor) {
			sb.append(resourceRepository().getString("DESCRIPTOR_TYPE_OX"));
		}
			
		sb.append(buildText(value));

		return sb.toString();
	}

}
