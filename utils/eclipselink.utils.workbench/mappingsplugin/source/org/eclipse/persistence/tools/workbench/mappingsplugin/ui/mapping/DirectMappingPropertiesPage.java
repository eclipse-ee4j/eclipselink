/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;


public abstract class DirectMappingPropertiesPage 
	extends TabbedPropertiesPage
{
	// **************** Constructors ******************************************
	
	protected DirectMappingPropertiesPage(WorkbenchContext context) {
		super(context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeTabs() {
		this.addTab(this.buildGeneralPanel(), "GENERAL_TAB");
		this.addTab(this.buildConverterPanel(), "DIRECT_MAPPING_CONVERTER_TAB");
	}
	
	protected abstract Component buildGeneralPanel();
	
	protected ConverterPropertiesPage buildConverterPanel() {
		return new ConverterPropertiesPage(this.getNodeHolder(), getWorkbenchContextHolder());
	}
}
