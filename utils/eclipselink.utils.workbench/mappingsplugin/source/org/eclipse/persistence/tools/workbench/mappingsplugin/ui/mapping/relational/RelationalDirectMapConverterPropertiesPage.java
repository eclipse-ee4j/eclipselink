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

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ConverterPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class RelationalDirectMapConverterPropertiesPage 
	extends AbstractPropertiesPage
{
		
	RelationalDirectMapConverterPropertiesPage(PropertyValueModel converterMappingNodeHolder, WorkbenchContextHolder contextHolder) {
		super(converterMappingNodeHolder, contextHolder);
	}
	
	protected void initializeLayout() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ConverterPanel keyConverterPanel =
			new ConverterPanel(
				this.buildDirectKeyConverterHolder(), 
				this.buildDirectKeyConverterSetter(), 
				this.getWorkbenchContextHolder()
			);

		JScrollPane scrollPane = new JScrollPane(keyConverterPanel);
		scrollPane.setBorder(null);
		tabbedPane.addTab(
			resourceRepository().getString("KEY_CONVERTER_TAB_TITLE"),
			scrollPane
		);

		ConverterPanel valueConverterPanel = 
			new ConverterPanel(
				this.buildDirectValueConverterHolder(), 
				this.buildDirectValueConverterSetter(), 
				this.getWorkbenchContextHolder()
			);

		scrollPane = new JScrollPane(valueConverterPanel);
		scrollPane.setBorder(null);
		tabbedPane.addTab(
			resourceRepository().getString("VALUE_CONVERTER_TAB_TITLE"),
			scrollPane
		);

		add(tabbedPane, BorderLayout.CENTER);
	}
	
	private PropertyValueModel buildDirectValueConverterHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWDirectMapMapping.CONVERTER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDirectMapMapping) subject).getConverter();
			}
		};
	}	

	private ConverterPanel.ConverterSetter buildDirectValueConverterSetter() {
		return new  ConverterPanel.ConverterSetter() {
			public String getType() {
				return ((MWDirectMapMapping) selection()).getConverter().getType();
			}

			public void setNullConverter() {
				((MWDirectMapMapping) selection()).setNullConverter();
			}

			public void setObjectTypeConverter() {
				((MWDirectMapMapping) selection()).setObjectTypeConverter();
			}

			public void setSerializedObjectConverter() {
				((MWDirectMapMapping) selection()).setSerializedObjectConverter();
			}

			public void setTypeConversionConverter() {
				((MWDirectMapMapping) selection()).setTypeConversionConverter();
			}

			public String converterTypePropertyString() {
				return MWDirectMapMapping.CONVERTER_PROPERTY;
			}
		};
	}	

	private PropertyValueModel buildDirectKeyConverterHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWDirectMapMapping.DIRECT_KEY_CONVERTER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDirectMapMapping) subject).getDirectKeyConverter();
			}
		};
	}	

	private ConverterPanel.ConverterSetter buildDirectKeyConverterSetter() {
		return new  ConverterPanel.ConverterSetter() {
			public String getType() {
				return ((MWDirectMapMapping) selection()).getDirectKeyConverter().getType();
			}

			public void setNullConverter() {
				((MWDirectMapMapping) selection()).setNullDirectKeyConverter();
			}

			public void setObjectTypeConverter() {
				((MWDirectMapMapping) selection()).setObjectTypeDirectKeyConverter();
			}

			public void setSerializedObjectConverter() {
				((MWDirectMapMapping) selection()).setSerializedObjectDirectKeyConverter();
			}

			public void setTypeConversionConverter() {
				((MWDirectMapMapping) selection()).setTypeConversionDirectKeyConverter();
			}

			public String converterTypePropertyString() {
				return MWDirectMapMapping.DIRECT_KEY_CONVERTER_PROPERTY;
			}
		};
	}	
}
