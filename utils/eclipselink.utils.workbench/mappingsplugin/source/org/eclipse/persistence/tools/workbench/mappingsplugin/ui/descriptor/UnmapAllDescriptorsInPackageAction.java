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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

final class UnmapAllDescriptorsInPackageAction extends AbstractFrameworkAction {

	UnmapAllDescriptorsInPackageAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		super.initialize();
		this.setIcon(EMPTY_ICON);
		this.initializeTextAndMnemonic("UNMAP_ALL_DESCRIPTORS_IN_PACKAGE_ACTION");
		this.initializeToolTipText("UNMAP_ALL_DESCRIPTORS_IN_PACKAGE_ACTION.toolTipText");
	}

	protected void execute(ApplicationNode selectedNode) {
		navigatorSelectionModel().pushExpansionState();
		((UnmappablePackageNode) selectedNode).unmapEntirePackage();
		navigatorSelectionModel().popAndRestoreExpansionState();		
	}
}
