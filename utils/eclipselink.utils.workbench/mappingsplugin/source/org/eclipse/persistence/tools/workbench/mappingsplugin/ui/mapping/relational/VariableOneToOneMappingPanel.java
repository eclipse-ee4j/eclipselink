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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ProxyIndirectionPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


final class VariableOneToOneMappingPanel extends ScrollablePropertiesPage {

	VariableOneToOneMappingPanel(PropertyValueModel mappingNodeHolder, WorkbenchContextHolder contextHolder) {
		super(mappingNodeHolder, contextHolder);
	}
	
	protected String helpTopicId() {
		return "mapping.variableOneToOne";
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(panel);
		offset.left += 5; offset.right += 5;
		
		// Reference Descriptor chooser
		JComponent referenceDescriptorWidgets =
			buildLabeledComponent("REFERENCE_DESCRIPTOR_CHOOSER_LABEL", buildReferenceDescriptorChooser());
		addHelpTopicId(referenceDescriptorWidgets, helpTopicId() + ".referenceDescriptor");
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, offset.left, 0, offset.right);
		panel.add(referenceDescriptorWidgets, constraints);
		
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
		constraints.insets      = new Insets(5, 0, 0, 0);
		panel.add(methodAccessingPanel, constraints);
		
		// Read only check box
		JCheckBox readOnlyCheckBox = 
			RelationalMappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);
		panel.add(readOnlyCheckBox, constraints);
		
		// Private owned check box
		JCheckBox privateOwnedCheckBox = buildPrivateOwnedCheckBox();
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);
		panel.add(privateOwnedCheckBox, constraints);
		
		// Use indirection check box
        ProxyIndirectionPanel indirectionPanel = new ProxyIndirectionPanel(getSelectionHolder(), getApplicationContext());
		constraints.gridx		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);
		panel.add(indirectionPanel, constraints);
		
		// Use indirection check box
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 6;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 5);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");
		
		addHelpTopicId(panel, helpTopicId());

		return panel;
	}
	
	
	// ************ Reference Descriptor *****************
	
	private ListChooser buildReferenceDescriptorChooser() {
		return RelationalMappingComponentFactory.buildReferenceDescriptorChooser(
			this.getSelectionHolder(), 
			this.getWorkbenchContextHolder()
		);
	}

	// ************* private owned ************
	
	protected JCheckBox buildPrivateOwnedCheckBox() {
		return MappingComponentFactory.buildPrivateOwnedCheckBox(
			this.getSelectionHolder(), 
			this.resourceRepository(),
			this.helpManager()
		);
	}
		
}
