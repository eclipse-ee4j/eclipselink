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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.UiDbBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;


final class OneToManyMappingTabbedPropertiesPage extends TabbedPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiMappingBundle.class,
		UiMappingRelationalBundle.class,
		UiDbBundle.class
	};


	OneToManyMappingTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		addTab(new OneToManyGeneralPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()),        "ONE_TO_MANY_GENERAL_TAB_TITLE");
		addTab(new OneToManyTableReferencePropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "ONE_TO_MANY_TABLE_REFERENCE_TAB_TITLE");
		addTab(new OrderingPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()),                "ONE_TO_MANY_ORDERING_TAB_TITLE");
	}
}
