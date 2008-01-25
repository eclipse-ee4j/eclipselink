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

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ChangeDescriptorTypeAction;


final class EisCompositeDescriptorAction extends ChangeDescriptorTypeAction {
	
	
	EisCompositeDescriptorAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		initializeTextAndMnemonic("EIS_COMPOSITE_DESCRIPTOR_ACTION");
		initializeIcon("descriptor.eis.composite");
		initializeToolTipText("EIS_COMPOSITE_DESCRIPTOR_ACTION.toolTipText");
	}

	protected MWDescriptor morphDescriptor(MWDescriptor descriptor) {
		try {
			return  ((MWEisDescriptor) descriptor).asCompositeEisDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException("This should not happen during morphing", e);
		}
	}
	
	protected boolean shouldBeSelected(ApplicationNode selectedNode) {
		return ((EisDescriptorNode) selectedNode).isCompositeDescriptor();
	}
}
