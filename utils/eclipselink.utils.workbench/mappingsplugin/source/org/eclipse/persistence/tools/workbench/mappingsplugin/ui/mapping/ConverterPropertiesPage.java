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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



public final class ConverterPropertiesPage 
	extends ScrollablePropertiesPage
{	
	public ConverterPropertiesPage(PropertyValueModel converterMappingNodeHolder, WorkbenchContextHolder contextHolder) {
		super(converterMappingNodeHolder, contextHolder);
	}
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		ConverterPanel converterPanel = new ConverterPanel(buildConverterHolder(), buildConverterSetter(), getWorkbenchContextHolder());
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0,0,0,0);
		panel.add(converterPanel, constraints);
		
		return panel;
	}
	
	private PropertyValueModel buildConverterHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWConverterMapping.CONVERTER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWConverterMapping) subject).getConverter();
			}
		};
	}

	private ConverterPanel.ConverterSetter buildConverterSetter() {
		return new  ConverterPanel.ConverterSetter() {
			public String getType() {
				return ((MWConverterMapping) selection()).getConverter().getType();
			}

			public void setNullConverter() {
				((MWConverterMapping) selection()).setNullConverter();
			}

			public void setObjectTypeConverter() {
				((MWConverterMapping) selection()).setObjectTypeConverter();
			}

			public void setSerializedObjectConverter() {
				((MWConverterMapping) selection()).setSerializedObjectConverter();
			}

			public void setTypeConversionConverter() {
				((MWConverterMapping) selection()).setTypeConversionConverter();
			}

			public String converterTypePropertyString() {
				return MWConverterMapping.CONVERTER_PROPERTY;
			}
		};
	}
}
