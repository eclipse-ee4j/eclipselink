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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.NullValuePolicyPanel;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


final class DirectToFieldMappingPanel extends ScrollablePropertiesPage {

	private ValueModel parentDescriptorHolder;

	DirectToFieldMappingPanel(PropertyValueModel mappingNodeHolder, WorkbenchContextHolder contextHolder) {
		super(mappingNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.parentDescriptorHolder = buildParentDescriptorHolder();
	}

	protected MWDirectToFieldMapping getDirectMapping() {
		return (MWDirectToFieldMapping) selection();
	}
	
	protected String helpTopicId() {
		return "mapping.directToField";
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(panel);
		offset.left += 5; offset.right += 5;

		ListChooser databaseFieldChooser = buildDatabaseFieldChooser();
		addHelpTopicId(databaseFieldChooser, helpTopicId() + ".databaseField");
		new ComponentEnabler(buildDatabaseFieldChooserEnablerModel(), Collections.singleton(databaseFieldChooser));

		JComponent databaseFieldWidget = buildLabeledComponent(
			"DATABASE_FIELD_CHOOSER_LABEL",
			databaseFieldChooser
		);

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, offset.left, 0, offset.right);
		panel.add(databaseFieldWidget, constraints);

		MethodAccessingPanel methodAccessingPanel = new MethodAccessingPanel(getSelectionHolder(), getWorkbenchContextHolder());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(methodAccessingPanel, constraints);
		addPaneForAlignment(methodAccessingPanel);
		
		NullValuePolicyPanel useWhenNullPolicyPanel = new NullValuePolicyPanel(getSelectionHolder(), getWorkbenchContextHolder());
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(useWhenNullPolicyPanel, constraints);
		addPaneForAlignment(useWhenNullPolicyPanel);
		
		JCheckBox readOnlyCheckBox = 
			RelationalMappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);
		panel.add(readOnlyCheckBox, constraints);

		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
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
				return ((MWDirectToFieldMapping) subject).getParentDescriptor();
			}
		};
	}

	private ValueModel buildDatabaseFieldChooserEnablerModel() {
		return new PropertyAspectAdapter(parentDescriptorHolder) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(!((MWRelationalDescriptor) subject).isAggregateDescriptor());
			}
		};
	}
}
