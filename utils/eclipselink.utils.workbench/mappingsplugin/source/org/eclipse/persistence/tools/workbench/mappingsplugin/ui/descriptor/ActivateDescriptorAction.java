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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;


final class ActivateDescriptorAction extends AbstractToggleFrameworkAction {
	
	ActivateDescriptorAction(WorkbenchContext context) 
	{		
		super(context);
	}

	protected void initialize() 
	{
		super.initialize();
		this.setIcon(EMPTY_ICON);
		initializeTextAndMnemonic("ACTIVATE_DESCRIPTOR_ACTION");
		initializeToolTipText("ACTIVATE_DESCRIPTOR_ACTION");
	}
	
	protected void execute() {
		boolean active = !isSelected();
		ApplicationNode[] nodes = this.selectedNodes();
		for (int i = nodes.length; i-- > 0; ) {
			((MWDescriptor) nodes[i].getValue()).setActive(active);
		}
	}
	
	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return true;
	}

	protected boolean shouldBeSelected(ApplicationNode selectedNode) {
		return ((DescriptorNode) selectedNode).getDescriptor().isActive();
	}
	
	protected String[] selectedPropertyNames() {
		return new String[] {MWDescriptor.ACTIVE_PROPERTY};
	}

	protected ApplicationNode[] buildSelectedNodes() {
		return selectedProjectNodes();
	}
}
