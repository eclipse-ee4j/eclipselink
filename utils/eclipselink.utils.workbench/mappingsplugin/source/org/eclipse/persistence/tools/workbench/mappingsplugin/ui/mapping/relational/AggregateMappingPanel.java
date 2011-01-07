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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;



final class AggregateMappingPanel extends ScrollablePropertiesPage {

	AggregateMappingPanel(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	private String helpTopicId() {
		return "mapping.aggregate";
	}
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(panel);
		offset.left += 5; offset.right += 5;
		
		// Reference Descriptor chooser
		JComponent referenceDescriptorWidgets =
			buildLabeledComponent("REFERENCE_DESCRIPTOR_CHOOSER_LABEL", buildReferenceDescriptorChooser());
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, offset.left, 0, offset.right);
		panel.add(referenceDescriptorWidgets, constraints);
		helpManager().addTopicID(referenceDescriptorWidgets, "mapping.aggregate.referenceDescriptor");
		
		// Method accessing panel
		MethodAccessingPanel methodAccessingPanel = 
			new MethodAccessingPanel(getSelectionHolder(), getWorkbenchContextHolder());
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
		
		// Read only check box
		JCheckBox readOnlyCheckBox = 
			MappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);
		panel.add(readOnlyCheckBox, constraints);
		
		// Allows null check box
		JCheckBox allowsNullCheckBox = this.buildAllowsNullCheckBox();
		addHelpTopicId(allowsNullCheckBox, helpTopicId() + ".allowsNull");
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		panel.add(allowsNullCheckBox, constraints);
		
		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		addHelpTopicId(commentPanel, "mapping.comment");
		constraints.gridx		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 5);
		panel.add(commentPanel, constraints);
		
		addHelpTopicId(panel, helpTopicId());
		
		return panel;
	}


	// ************* reference descriptor ************

	
	private ListChooser buildReferenceDescriptorChooser() {
		return MappingComponentFactory.buildReferenceDescriptorChooser(getSelectionHolder(), getWorkbenchContextHolder());
	}


	
	// ************* allows null ************
	
	private JCheckBox buildAllowsNullCheckBox() {
		return buildCheckBox("AGGREGATE_MAPPING_ALLOWS_NULL_CHECK_BOX", new CheckBoxModelAdapter(buildAllowsNullHolder()));
	}
	
	
	private PropertyValueModel buildAllowsNullHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWAggregateMapping.ALLOWS_NULL_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWAggregateMapping) subject).allowsNull());
			}
			protected void setValueOnSubject(Object value) {
				((MWAggregateMapping) subject).setAllowsNull(((Boolean) value).booleanValue());
			}
		};
	} 
	
}
