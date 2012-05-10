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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ProxyIndirectionPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisOneToOneGeneralPropertiesPage extends ScrollablePropertiesPage
{
	// **************** Constructors ******************************************
	
	EisOneToOneGeneralPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected Component buildPage() {
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;
		
		// Reference Descriptor chooser
		JComponent referenceDescriptorComponent = this.buildReferenceDescriptorChooserPanel();
		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, offset.left, 0, offset.right);
		container.add(referenceDescriptorComponent, constraints);
		
		// Foreign keys panel
		ForeignKeysSubPane foreignKeysPanel = this.buildForeignKeysPanel();
		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);
		container.add(foreignKeysPanel, constraints);
		addPaneForAlignment(foreignKeysPanel);
		
		// Bidirectional relationship panel
		MaintainsBidirectionalRelationshipPanel bidirectionalRelationshipPanel = 
			new MaintainsBidirectionalRelationshipPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);
		container.add(bidirectionalRelationshipPanel, constraints);
		addPaneForAlignment(bidirectionalRelationshipPanel);
		
		// Method accessing panel
		MethodAccessingPanel methodAccessingPanel = 
			new MethodAccessingPanel(getSelectionHolder(), getWorkbenchContextHolder());
		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);
		container.add(methodAccessingPanel, constraints);
		addPaneForAlignment(methodAccessingPanel);
		
		// Read Only
		JCheckBox readOnlyCheckBox = this.buildReadOnlyCheckBox();
		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);
		container.add(readOnlyCheckBox, constraints);

		// Private Owned
		JCheckBox privateOwnedCheckBox = this.buildPrivateOwnedCheckBox();
		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);
		container.add(privateOwnedCheckBox, constraints);
		
		// Use Indirection
        ProxyIndirectionPanel indirectionPanel = new ProxyIndirectionPanel(getSelectionHolder(), getApplicationContext());
		constraints.gridx       = 0;
		constraints.gridy       = 6;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);
		container.add(indirectionPanel, constraints);

		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 7;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);
		container.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");
		
		addHelpTopicId(container, "mapping.eisOneToOne.general");
		
		return container;
	}
	
	
	// **************** Reference descriptor chooser panel ********************
	
	private JComponent buildReferenceDescriptorChooserPanel() {
		JComponent component =
			this.buildLabeledComponent(
				"REFERENCE_DESCRIPTOR_CHOOSER_LABEL",
				MappingComponentFactory.buildReferenceDescriptorChooser(
					getSelectionHolder(),
					getWorkbenchContextHolder()
				)
			);
		addHelpTopicId(component, "mapping.referenceDescriptor");
		return component;
	}
		
	
	// **************** Field pairs panel *************************************
	
	private ForeignKeysSubPane buildForeignKeysPanel() {
		return new ForeignKeysSubPane();
	}
	
	
	// **************** Read only check box ***********************************
	
	private JCheckBox buildReadOnlyCheckBox() {
		return MappingComponentFactory.buildReadOnlyCheckBox(
			this.getSelectionHolder(), 
			this.getApplicationContext()
		);
	}
	
	
	// **************** Private owned check box *******************************
	
	private JCheckBox buildPrivateOwnedCheckBox() {
		return MappingComponentFactory.buildPrivateOwnedCheckBox(
			this.getSelectionHolder(), 
			this.resourceRepository(),
			this.helpManager()
		);
	}	
	
	
	// **************** Member classes ****************************************
	
	private class ForeignKeysSubPane 
		extends AbstractSubjectPanel 
	{
		private ForeignKeysSubPane() {
			super(
				EisOneToOneGeneralPropertiesPage.this.getSelectionHolder(),
				EisOneToOneGeneralPropertiesPage.this.getWorkbenchContextHolder()
			);
		}
		
		protected void initializeLayout() {
			GridBagConstraints constraints = new GridBagConstraints();

			setBorder(
				BorderFactory.createCompoundBorder(
					buildTitledBorder("FOREIGN_KEYS_TITLE"),
					BorderFactory.createEmptyBorder(0, 5, 5, 5)
				)
			);
			
			// Field Pairs label
			JLabel fieldPairsLabel = buildLabel("FIELD_PAIR_TABLE");
			constraints.gridx       = 0;
			constraints.gridy       = 0;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 0;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.NONE;
			constraints.anchor      = GridBagConstraints.LINE_START;
			constraints.insets      = new Insets(0, 0, 0, 0);
			this.add(fieldPairsLabel, constraints);
			this.addAlignLeft(fieldPairsLabel);
			this.addHelpTopicId(fieldPairsLabel, "mapping.eisOneToOne.fieldPairs");
			
			// Field Pairs table
			EisReferenceMappingFieldPairsPanel fieldPairsPanel = new EisReferenceMappingFieldPairsPanel(getSelectionHolder(), getWorkbenchContextHolder());
			constraints.gridx       = 0;
			constraints.gridy       = 1;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 1;
			constraints.weighty     = 1;
			constraints.fill        = GridBagConstraints.BOTH;
			constraints.anchor      = GridBagConstraints.CENTER;
			constraints.insets      = new Insets(2, -1, 0, 0);
			this.add(fieldPairsPanel, constraints);
			this.addPaneForAlignment(fieldPairsPanel);

			addHelpTopicId(this, "mapping.eisOneToOne.foreignKeys");

		}
	}
}
