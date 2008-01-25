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

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ChangeDescriptorTypeAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;



final class EisRootDescriptorAction extends ChangeDescriptorTypeAction {
	
	EisRootDescriptorAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		initializeTextAndMnemonic("EIS_ROOT_DESCRIPTOR_ACTION");
		initializeIcon("descriptor.eis.root");
		initializeToolTipText("EIS_ROOT_DESCRIPTOR_ACTION.toolTipText");
	}

	protected MWDescriptor morphDescriptor(MWDescriptor descriptor) {
		return  ((MWEisDescriptor) descriptor).asRootEisDescriptor();
	}
	
	protected boolean shouldBeSelected(ApplicationNode selectedNode) {
		return ((EisDescriptorNode) selectedNode).isRootDescriptor();
	}
}
