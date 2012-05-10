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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;



final class VariableOneToOneMappingTabbedPropertiesPage extends TabbedPropertiesPage {

	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiDescriptorBundle.class,
		UiMappingBundle.class,
		UiMappingRelationalBundle.class
	};


	VariableOneToOneMappingTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		addTab(new VariableOneToOneMappingPanel(getNodeHolder(), getWorkbenchContextHolder()), "VARIABLE_ONE_TO_ONE_GENERAL_TAB");
		addTab(new VariableOneToOneQueryKeyAssociationsPanel(getNodeHolder(), getWorkbenchContextHolder()), "VARIABLE_ONE_TO_ONE_QUERY_KEY_ASSOCIATIONS_TAB");
		addTab(new VariableOneToOneClassIndicatorsPanel(getNodeHolder(), getWorkbenchContextHolder()), "VARIABLE_ONE_TO_ONE_CLASS_INDICATOR_INFO_TAB");
	}

}
