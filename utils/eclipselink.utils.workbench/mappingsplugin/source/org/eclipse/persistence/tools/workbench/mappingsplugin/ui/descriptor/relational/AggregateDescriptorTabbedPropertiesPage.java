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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;


import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.EventsPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational.RelationalDescriptorQueryKeysPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class AggregateDescriptorTabbedPropertiesPage extends MappingDescriptorTabbedPropertiesPage 
{
	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiQueryBundle.class,
		UiDescriptorBundle.class,
		UiDescriptorRelationalBundle.class
	};


    private RelationalDescriptorQueryKeysPropertiesPage queryKeysPage;
    
	AggregateDescriptorTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		super.initializeTabs();
        this.queryKeysPage = new RelationalDescriptorQueryKeysPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
		addTab(buildClassInfoPropertiesPage(), "CLASS_INFO_TAB");
		addTab(this.queryKeysPage, "RELATIONAL_DESCRIPTOR_QUERY_KEYS_TAB");
		addTab(buildEventsPolicyValueModel(), EventsPolicyPropertiesPage.EDITOR_WEIGHT, 
				buildEventsPolicyPropertiesPage(), "RELATIONAL_DESCRIPTOR_EVENTS_TAB");
	}

	protected Component buildInheritancePolicyPropertiesPage() {
		return new RelationalInheritancePolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	/**
	 * InheritancePolicy <code>ComponentBuilder</code> for dynamic tab.
	 */
	protected ComponentBuilder buildInheritancePolicyPageBuilder() {
		return new ComponentBuilder() {
			private RelationalInheritancePolicyPropertiesPage inheritancePolicyPage;
			
			public Component buildComponent(PropertyValueModel nodeHolder) {
				if (inheritancePolicyPage == null) {
						inheritancePolicyPage = new RelationalInheritancePolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
					}
					return inheritancePolicyPage;
			}
		};
	}
    
    public void selectQueryKey(MWQueryKey queryKey) {
        setSelectedTab(this.queryKeysPage);
        this.queryKeysPage.selectQueryKey(queryKey);
    }

}
