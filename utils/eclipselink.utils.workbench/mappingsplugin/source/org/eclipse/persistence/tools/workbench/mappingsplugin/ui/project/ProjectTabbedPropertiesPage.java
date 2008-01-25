/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;


public abstract class ProjectTabbedPropertiesPage extends TabbedPropertiesPage {

	protected ProjectTabbedPropertiesPage(WorkbenchContext context) {
		super(context.buildExpandedResourceRepositoryContext(UiProjectBundle.class));
	}
	
	protected Component buildProjectGeneralPropertiesPage() {
		return new ProjectGeneralPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}
	
	protected Component buildProjectDefaultsPropertiesPage() {
		return new ProjectDefaultsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

}
