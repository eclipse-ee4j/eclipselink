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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.TransactionalDescriptorComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaContextChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaRootElementChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisRootDescriptorInfoPropertiesPage 
	extends ScrollablePropertiesPage 
{
	EisRootDescriptorInfoPropertiesPage(PropertyValueModel eisDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(eisDescriptorNodeHolder, contextHolder);
	}
		
	private String helpTopicId() {
		return "eisRootDescriptor.descriptorInfo";
	}
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(panel);
		offset.left += 5; offset.right += 5;

		// schema context chooser label
		JLabel schemaContextLabel = XmlDescriptorComponentFactory.buildSchemaContextLabel(resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, offset.left, 0, 0);
		panel.add(schemaContextLabel, constraints);
		addAlignLeft(schemaContextLabel);
		
		// schema context chooser
		SchemaContextChooser schemaContextChooser = XmlDescriptorComponentFactory.buildSchemaContextChooser(getSelectionHolder(), getWorkbenchContextHolder(), schemaContextLabel);
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, offset.right);
		panel.add(schemaContextChooser, constraints);
		addPaneForAlignment(schemaContextChooser);
		
		// root element label
		JLabel defaultRootElementLabel = XmlDescriptorComponentFactory.buildDefaultRootElementLabel(resourceRepository());
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.LINE_START;
		constraints.insets 		= new Insets(5, offset.left, 0, 0);
		panel.add(defaultRootElementLabel, constraints);
		addAlignLeft(schemaContextLabel);
		helpManager().addTopicID(defaultRootElementLabel, "eisRootDescriptor.defaultRootElement");
	  	
		SchemaRootElementChooser chooser = XmlDescriptorComponentFactory.buildDefaultRootElementChooser(getSelectionHolder(), getWorkbenchContextHolder(), defaultRootElementLabel);
		constraints.gridx 		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 5, 0, offset.right);
		panel.add(chooser, constraints);
		addPaneForAlignment(chooser);
		helpManager().addTopicID(chooser, "eisRootDescriptor.defaultRootElement");

		// primary keys - panel
		EisPrimaryKeysPanel primaryKeysPanel = this.buildPrimaryKeysPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(primaryKeysPanel, constraints);
		addPaneForAlignment(primaryKeysPanel);

		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);
		panel.add(TransactionalDescriptorComponentFactory.buildReadOnlyCheckBox(getSelectionHolder(), getApplicationContext()), constraints);
	
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);
		panel.add(TransactionalDescriptorComponentFactory.buildConformResultsInUnitOfWorkCheckBox(getSelectionHolder(), getApplicationContext()), constraints);

        
        // Descriptor Alias widget
        JComponent descriptorAliasWidget = buildLabeledTextField(
            "DESCRIPTOR_ALIAS_LABEL",
            TransactionalDescriptorComponentFactory.buildDescriptorAliasDocumentAdapter(getSelectionHolder())
        );

        constraints.gridx      = 0;
        constraints.gridy      = 5;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, offset.left, 0, 5);

        panel.add(descriptorAliasWidget, constraints);
        addHelpTopicId(descriptorAliasWidget, helpTopicId() + ".alias");

        // comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 6;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 5);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "descriptor.general.comment");
	
		addHelpTopicId(panel, helpTopicId());
		
		return panel;
	}


	// **************** Primary keys ******************************************
	
	private EisPrimaryKeysPanel buildPrimaryKeysPanel() {
		return new EisPrimaryKeysPanel(this.getSelectionHolder(), getWorkbenchContextHolder());
	}
	
}

