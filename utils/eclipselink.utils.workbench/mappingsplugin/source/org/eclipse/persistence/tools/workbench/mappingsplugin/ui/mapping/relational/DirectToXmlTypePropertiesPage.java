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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;



final class DirectToXmlTypePropertiesPage extends TitledPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiMappingBundle.class,
		UiMappingRelationalBundle.class
	};


	private ValueModel parentDescriptorHolder;

	DirectToXmlTypePropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.parentDescriptorHolder = buildParentDescriptorHolder();
	}

	protected String helpTopicId() {
		return "mapping.directToXmlType";
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(panel);
		offset.left += 5; offset.right += 5;

		JComponent databaseFieldWidget = buildLabeledComponent(
			"DATABASE_FIELD_CHOOSER_LABEL",
			buildDatabaseFieldChooser()
		);

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, offset.left, 0, offset.right);
		panel.add(databaseFieldWidget, constraints);
		helpManager().addTopicID(databaseFieldWidget, "mapping.directToXmlType.databaseField");

		MethodAccessingPanel methodAccessingPanel = new MethodAccessingPanel(getSelectionHolder(), getWorkbenchContextHolder());
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(methodAccessingPanel, constraints);
		addPaneForAlignment(methodAccessingPanel);
				
		JCheckBox readOnlyCheckBox = buildReadOnlyCheckBox();
		addHelpTopicId(readOnlyCheckBox, helpTopicId() + ".readOnly");
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);
		panel.add(readOnlyCheckBox, constraints);	

		JCheckBox readWholeDocumentCheckBox = buildReadWholeDocumentCheckBox();
		addHelpTopicId(readWholeDocumentCheckBox, helpTopicId() + ".readWholeDocument");
		constraints.gridx			= 0;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		panel.add(readWholeDocumentCheckBox, constraints);

		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx			= 0;
		constraints.gridy			= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 5);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");

		addHelpTopicId(panel, helpTopicId());
	
		return panel;
	}
	
	// ************* database field ************

	private ListChooser buildDatabaseFieldChooser() {
		return RelationalMappingComponentFactory.buildColumnChooser(getSelectionHolder(), parentDescriptorHolder, getWorkbenchContextHolder());
	}
	
	private PropertyValueModel buildParentDescriptorHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWDirectToXmlTypeMapping) subject).getParentDescriptor();
			}
		};
	}
	
	
	// ************* read only ************
	
	protected JCheckBox buildReadOnlyCheckBox() {
		return RelationalMappingComponentFactory.buildReadOnlyCheckBox(
			this.getSelectionHolder(), 
			this.getApplicationContext()
		);
	}

	
	// *********** Read Whole Document ************
	
	private JCheckBox buildReadWholeDocumentCheckBox() {
		JCheckBox checkBox = buildCheckBox("READ_WHOLE_DOCUMENT_CHECK_BOX", buildReadWholeDocumentButtonModel());
		return checkBox;
	}
	private ButtonModel buildReadWholeDocumentButtonModel() {
		return new CheckBoxModelAdapter(buildReadWholeDocumentAdapter());
	}
	
	private PropertyValueModel buildReadWholeDocumentAdapter() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWDirectToXmlTypeMapping.READ_WHOLE_DOCUMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWDirectToXmlTypeMapping) subject).isReadWholeDocument());
			}
			protected void setValueOnSubject(Object value) {
				((MWDirectToXmlTypeMapping) subject).setReadWholeDocument(((Boolean) value).booleanValue());
			}
		};
	}

}
