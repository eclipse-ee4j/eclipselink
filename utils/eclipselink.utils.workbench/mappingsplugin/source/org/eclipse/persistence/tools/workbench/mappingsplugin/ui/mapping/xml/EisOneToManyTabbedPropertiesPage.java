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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;


final class EisOneToManyTabbedPropertiesPage extends TabbedPropertiesPage
{
	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiXmlBundle.class,
		UiMappingBundle.class,
		UiMappingXmlBundle.class,
		UiQueryBundle.class
	};


	EisOneToManyTabbedPropertiesPage(WorkbenchContext context)
	{
		super(context);
	}

	protected void initializeTabs()
	{
		addTab(new EisOneToManyGeneralPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "GENERAL_TAB_TITLE");
		addTab(new EisOneToManySelectionInteractionPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "SELECTION_INTERACTION_TAB_TITLE");
		addTab(new EisOneToManyDeleteAllInteractionPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "DELETE_ALL_INTERACTION");
	}
}
