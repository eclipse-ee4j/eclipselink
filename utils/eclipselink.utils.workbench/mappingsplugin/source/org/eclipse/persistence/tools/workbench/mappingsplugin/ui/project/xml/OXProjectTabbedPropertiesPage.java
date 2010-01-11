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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.UiProjectBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;


final class OXProjectTabbedPropertiesPage extends ProjectTabbedPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiXmlBundle.class,
		UiProjectBundle.class
	};


	OXProjectTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		addTab(buildProjectGeneralPropertiesPage(),  "GENERAL_TAB_TITLE");
		addTab(buildProjectDefaultsPropertiesPage(), "DEFAULTS_TAB_TITLE");
		addTab(buildProjectOptionsPropertiesPage(),  "OPTIONS_TAB_TITLE");
	}
	
	protected Component buildProjectGeneralPropertiesPage() {
		return new XmlProjectGeneralPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected Component buildProjectOptionsPropertiesPage() {
		return new XmlProjectOptionsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}
}
