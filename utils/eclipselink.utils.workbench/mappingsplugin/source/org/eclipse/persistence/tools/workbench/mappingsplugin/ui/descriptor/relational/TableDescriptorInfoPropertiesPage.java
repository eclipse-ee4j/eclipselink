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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.TransactionalDescriptorComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class TableDescriptorInfoPropertiesPage 
	extends ScrollablePropertiesPage 
{
	
	// **************** Constructors ******************************************
	
	TableDescriptorInfoPropertiesPage(PropertyValueModel relationalDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(relationalDescriptorNodeHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(panel);
		offset.left += 5; offset.right += 5;

		// Associated Table - panel
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, offset.left, 0, offset.right);
		panel.add(this.buildPrimaryTablePanel(), constraints);

		// Primary Keys - panel
		TableDescriptorPrimaryKeysPanel primaryKeysPane = this.buildPrimaryKeysPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(primaryKeysPane, constraints);
		addPaneForAlignment(primaryKeysPane);
		
		// Sequencing - panel
		TableDescriptorSequencingPanel sequencingPanel = this.buildSequencingPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(sequencingPanel, constraints);
		addPaneForAlignment(sequencingPanel);
		
		// Read only - check box
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, offset.left, 0, 0);
		panel.add(TransactionalDescriptorComponentFactory.buildReadOnlyCheckBox(getSelectionHolder(), getApplicationContext()), constraints);
		
		// Conform results in unit of work - check box
		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, offset.left, 0, 0);
		panel.add(TransactionalDescriptorComponentFactory.buildConformResultsInUnitOfWorkCheckBox(getSelectionHolder(), getApplicationContext()), constraints);
		
        
        // Descriptor Alias widget
        JComponent descriptorAliasWidget = buildLabeledTextField(
            "DESCRIPTOR_ALIAS_LABEL",
            TransactionalDescriptorComponentFactory.buildDescriptorAliasDocumentAdapter(getSelectionHolder())
        );

        constraints.gridx      = 0;
        constraints.gridy      = 5;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, offset.left, 0, 5);

        panel.add(descriptorAliasWidget, constraints);
        addHelpTopicId(descriptorAliasWidget, helpTopicId() + ".alias");

        
        
		// comment text field
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx      = 0;
		constraints.gridy      = 6;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 5);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "descriptor.general.comment");
		
		this.addHelpTopicId(panel, this.helpTopicId());
		
		return panel;
	}
	
	
	// **************** Primary Table *****************************************
	
	private JComponent buildPrimaryTablePanel() {
		JComponent primaryTableWidgets = buildLabeledComponent(
			"associatedTable*",
			buildPrimaryTableChooser()
		);

		this.addHelpTopicId(primaryTableWidgets, this.helpTopicId() + ".primaryTable");

		return primaryTableWidgets;
	}
		
	// **************** Primary Table ******************************************

	private ListChooser buildPrimaryTableChooser() {
		return RelationalProjectComponentFactory.buildTableChooser(
			this.getSelectionHolder(),
			this.buildPrimaryTableHolder(),
			this.buildPrimaryTableChooserDialogBuilder(),
			this.getWorkbenchContextHolder()
		);
	}
	
	private PropertyValueModel buildPrimaryTableHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWTableDescriptor.PRIMARY_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) this.subject).getPrimaryTable();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWTableDescriptor) this.subject).setPrimaryTable((MWTable) value);
			}
		};
	}
	
	private DefaultListChooserDialog.Builder buildPrimaryTableChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("PRIMARY_TABLE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("PRIMARY_TABLE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTableStringConverter());
		return builder;
	}
	
	
	private StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTable) o).getName();
			}
		};
	}
	
	
	// **************** Primary keys ******************************************
	
	private TableDescriptorPrimaryKeysPanel buildPrimaryKeysPanel() {
		return new TableDescriptorPrimaryKeysPanel(this.getSelectionHolder(), getWorkbenchContextHolder());
	}
	
	
	// **************** Sequencing ********************************************
	
	private TableDescriptorSequencingPanel buildSequencingPanel() {
		TableDescriptorSequencingPanel sequencingPanel = 
			new TableDescriptorSequencingPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
		this.addHelpTopicId(sequencingPanel, this.helpTopicId() + ".sequencing");
		return sequencingPanel;
	}
	

	// **************** Help **************************************************
	
	private String helpTopicId() {
		return "descriptor.descriptorInfo";
	}
}
