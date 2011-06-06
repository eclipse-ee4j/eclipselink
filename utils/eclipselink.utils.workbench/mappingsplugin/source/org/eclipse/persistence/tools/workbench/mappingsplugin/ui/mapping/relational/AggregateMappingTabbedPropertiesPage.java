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

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class AggregateMappingTabbedPropertiesPage extends TabbedPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiMappingBundle.class,
		UiMappingRelationalBundle.class
	};


	AggregateMappingTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		addTab(new AggregateMappingPanel(getNodeHolder(), getWorkbenchContextHolder()), "AGGREGATE_MAPPING_GENERAL_TAB");
		addTab(buildInAggregateDescriptorValueModel(), DEFAULT_WEIGHT, buildFieldsPageBuilder(),"AGGREGATE_MAPPING_FIELDS_TAB");
	}


	
	private ComponentBuilder buildFieldsPageBuilder() {
		return new ComponentBuilder() {
			private AggregateMappingColumnsPanel aggregateMappingFieldsPanel;
			
			public Component buildComponent(PropertyValueModel nodeHolder) {
				if (aggregateMappingFieldsPanel == null) {
					aggregateMappingFieldsPanel = new AggregateMappingColumnsPanel(nodeHolder, getWorkbenchContextHolder());
				}
				return aggregateMappingFieldsPanel;
			}
		};
	}

	private PropertyValueModel buildInAggregateDescriptorValueModel() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object buildValue() {
				if (subject == null) {
					return Boolean.FALSE;
				}
				return this.getValueFromSubject();
			}

			protected Object getValueFromSubject() {
				if (((MWAggregateMapping) subject).parentDescriptorIsAggregate()) {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
		};
	}
	
}
