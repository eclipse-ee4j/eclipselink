/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;


public final class ManageNonDescriptorClassesAction
	extends AbstractEnablableFrameworkAction
{
	ManageNonDescriptorClassesAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		this.setIcon(EMPTY_ICON);
		this.initializeTextAndMnemonic("MANAGE_NON_DESCRIPTOR_CLASSES");
		this.initializeToolTipText("MANAGE_NON_DESCRIPTOR_CLASSES.toolTipText");
	}
	
	protected void execute() {
		// The assumption is that there is only one project node upon which to execute
		MWProject project = (MWProject) this.selectedNodes()[0].getProjectRoot().getValue();
		NonDescriptorClassManagementDialog dialog = 
			new NonDescriptorClassManagementDialog(this.getWorkbenchContext(), project);
		dialog.show();
	}
	
	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return this.navigatorSelectionModel().getSelectedProjectNodes().length == 1;
	}
}