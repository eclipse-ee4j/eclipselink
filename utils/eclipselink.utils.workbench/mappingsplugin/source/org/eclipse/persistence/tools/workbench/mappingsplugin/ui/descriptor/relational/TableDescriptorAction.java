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

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ChangeDescriptorTypeAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;


final class TableDescriptorAction 
	extends ChangeDescriptorTypeAction
{
	TableDescriptorAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		initializeIcon("descriptor.class");
		initializeText("MORPH_TO_CLASS_DESCRIPTOR_ACTION.text");
		initializeToolTipText("MORPH_TO_CLASS_DESCRIPTOR_ACTION.toolTipText");	
	}
	
	protected MWDescriptor morphDescriptor(MWDescriptor descriptor) {
		try {
			return  ((MWRelationalDescriptor) descriptor).asMWTableDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException("This should not happen during morphing", e);
		}
	}
	
	protected boolean shouldBeSelected(ApplicationNode selectedNode) {
		return ((RelationalClassDescriptorNode) selectedNode).isTableDescriptor();
	}
}
