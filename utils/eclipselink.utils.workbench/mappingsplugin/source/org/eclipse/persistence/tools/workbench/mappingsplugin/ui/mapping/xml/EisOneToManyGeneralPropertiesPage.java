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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.IndirectableContainerMappingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ToggleButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


final class EisOneToManyGeneralPropertiesPage 
	extends ScrollablePropertiesPage
{
	EisOneToManyGeneralPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
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

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, offset.left, 0, offset.right);

		panel.add(referenceDescriptorWidgets, constraints);
		addHelpTopicId(referenceDescriptorWidgets, "mapping.referenceDescriptor");

		// Foreign Keys sub-pane
		ForeignKeysSubPane foreignKeysSubPane = new ForeignKeysSubPane();

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(foreignKeysSubPane, constraints);
		addPaneForAlignment(foreignKeysSubPane);

		// Maintain Bidirectionality Relationship
		MaintainsBidirectionalRelationshipPanel maintainBidiRelationshipWidget = 
			new MaintainsBidirectionalRelationshipPanel(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(maintainBidiRelationshipWidget, constraints);
		addPaneForAlignment(maintainBidiRelationshipWidget);

		// Use Method accessing widgets
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
		panel.add(methodAccessingPanel, constraints);
		addPaneForAlignment(methodAccessingPanel);

		// Read Only
		JCheckBox readOnlyCheckBox = 
			MappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());

		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);

		panel.add(readOnlyCheckBox, constraints);

		// Private Owned
		JCheckBox privateOwnedCheckBox = 
			MappingComponentFactory.buildPrivateOwnedCheckBox(
				this.getSelectionHolder(), 
				this.resourceRepository(),
				this.helpManager()
			);
		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);
		panel.add(privateOwnedCheckBox, constraints);
		
		// Indirectable
		IndirectableContainerMappingPanel indirectablePanel = 
			new IndirectableContainerMappingPanel(getSelectionHolder(), getApplicationContext());
		constraints.gridx       = 0;
		constraints.gridy       = 6;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.BOTH;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);
		panel.add(indirectablePanel, constraints);

		// Collection Options Advanced button
		JPanel advancedPanel = MappingComponentFactory.buildContainerPolicyOptionsBrowser(
			getWorkbenchContextHolder(),
			getSelectionHolder(),
			"mapping.collectionOptions");
		constraints.gridx       = 0;
		constraints.gridy       = 7;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);
		panel.add(advancedPanel, constraints);

		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 8;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");

		addHelpTopicId(panel, "mapping.oneToMany.general");
		return panel;
	}

	private ListChooser buildReferenceDescriptorChooser() {
		return MappingComponentFactory.buildReferenceDescriptorChooser(getSelectionHolder(), getWorkbenchContextHolder());
	}

	private class ForeignKeysSubPane
		extends AbstractSubjectPanel
	{
		private ForeignKeysSubPane() {
			super(
				EisOneToManyGeneralPropertiesPage.this.getSelectionHolder(),
				EisOneToManyGeneralPropertiesPage.this.getWorkbenchContextHolder()
			);
		}

		protected void initializeLayout() {
			this.setBorder(
				BorderFactory.createCompoundBorder(
					this.buildTitledBorder("FOREIGN_KEYS_TITLE"),
					BorderFactory.createEmptyBorder(0, 5, 5, 5)
				)
			);
			int offset = SwingTools.checkBoxIconWidth();
			GridBagConstraints constraints = new GridBagConstraints();
			PropertyValueModel foreignKeyLocationHolder = buildForeignKeyLocationHolderValue();

			// Foreign keys on target button
			JRadioButton foreignKeysOnTargetButton =
				this.buildForeignKeysOnTargetButton(foreignKeyLocationHolder);
			constraints.gridx       = 0;
			constraints.gridy       = 0;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 0;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.NONE;
			constraints.anchor      = GridBagConstraints.LINE_START;
			constraints.insets      = new Insets(0, 0, 0, 0);
			this.add(foreignKeysOnTargetButton, constraints);
			addHelpTopicId(foreignKeysOnTargetButton, "mapping.eisOneToMany.foreignKeyOnTarget");

			// Foreign keys on source button
			JRadioButton foreignKeysOnSourceButton =
				this.buildForeignKeysOnSourceButton(foreignKeyLocationHolder);
			constraints.gridx       = 0;
			constraints.gridy       = 1;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 0;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.NONE;
			constraints.anchor      = GridBagConstraints.LINE_START;
			constraints.insets      = new Insets(0, 0, 0, 0);
			this.add(foreignKeysOnSourceButton, constraints);
			addHelpTopicId(foreignKeysOnSourceButton, "mapping.eisOneToMany.foreginKeyOnSource");

			// Field pairs subpanel
			AbstractPanel fieldPairsSubPane = new FieldPairsSubPane();
			constraints.gridx       = 0;
			constraints.gridy       = 2;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 1;
			constraints.weighty     = 1;
			constraints.fill        = GridBagConstraints.BOTH;
			constraints.anchor      = GridBagConstraints.CENTER;
			constraints.insets      = new Insets(0, offset, 0, 0);
			this.add(fieldPairsSubPane, constraints);
			this.addPaneForAlignment(fieldPairsSubPane);
			new ComponentEnabler(foreignKeyLocationHolder, fieldPairsSubPane);

			addHelpTopicId(this, "mapping.eisOneToMany.foreignKeys");
		}

		private JRadioButton buildForeignKeysOnTargetButton(PropertyValueModel foreignKeyLocationHolder) {
			JRadioButton button = 
				this.buildRadioButton(
					"FOREIGN_KEYS_ON_TARGET_BUTTON",
					this.buildForeignKeysOnTargetButtonModel(foreignKeyLocationHolder)
				);
			this.addHelpTopicId(button, "mapping.eisOneToMany.foreignKeyLocation");
			return button;
		}

		private ToggleButtonModelAdapter buildForeignKeysOnTargetButtonModel(PropertyValueModel foreignKeyLocationHolder) {
			return new RadioButtonModelAdapter(foreignKeyLocationHolder, Boolean.FALSE);
		}

		private PropertyValueModel buildForeignKeyLocationHolderValue() {
			return new PropertyAspectAdapter(this.getSubjectHolder(), MWEisOneToManyMapping.FOREIGN_KEY_LOCATION_PROPERTY) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf((((MWEisOneToManyMapping) this.subject).foreignKeysAreOnSource()));
				}
				
				protected void setValueOnSubject(Object value) {
					if (Boolean.FALSE.equals(value)) {
						((MWEisOneToManyMapping) this.subject).setForeignKeysOnTarget();
					}
					else if (Boolean.TRUE.equals(value)) {
						((MWEisOneToManyMapping) this.subject).setForeignKeysOnSource();
					}
				}
			};
		}

		private JRadioButton buildForeignKeysOnSourceButton(PropertyValueModel foreignKeyLocationHolder) {
			JRadioButton button = 
				this.buildRadioButton(
					"FOREIGN_KEYS_ON_SOURCE_BUTTON",
					this.buildForeignKeysOnSourceButtonModel(foreignKeyLocationHolder)
				);
			this.addHelpTopicId(button, "mapping.eisOneToMany.foreignKeyLocation");
			return button;
		}

		private ToggleButtonModelAdapter buildForeignKeysOnSourceButtonModel(PropertyValueModel foreignKeyLocationHolder) {
			return new RadioButtonModelAdapter(foreignKeyLocationHolder, Boolean.TRUE);
		}
	}
	
	private class FieldPairsSubPane
		extends AbstractSubjectPanel
	{
		private FieldPairsSubPane() {
			super(
				EisOneToManyGeneralPropertiesPage.this.getSelectionHolder(),
				EisOneToManyGeneralPropertiesPage.this.getWorkbenchContextHolder()
			);
		}
		
		protected void initializeLayout() {
			this.setLayout(new GridBagLayout());
			
			GridBagConstraints constraints = new GridBagConstraints();
			
			// Grouping Element label
			JLabel groupingElementLabel = 
				this.buildGroupingElementLabel();
			constraints.gridx       = 0;
			constraints.gridy       = 0;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 0;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.NONE;
			constraints.anchor      = GridBagConstraints.LINE_START;
			constraints.insets      = new Insets(0, 0, 0, 0);
			this.add(groupingElementLabel, constraints);
			this.addAlignLeft(groupingElementLabel);
			addHelpTopicId(groupingElementLabel, "mapping.eisOneToMany.foreignKeyGroupingElement");
			
			// Grouping Element chooser
			XpathChooser groupingElementChooser = 
				this.buildGroupingElementXpathChooser();
			groupingElementChooser.setAccessibleLabeler(groupingElementLabel);
			constraints.gridx       = 1;
			constraints.gridy       = 0;
			constraints.gridwidth   = 1;
			constraints.gridheight  = 1;
			constraints.weightx     = 1;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.HORIZONTAL;
			constraints.anchor      = GridBagConstraints.CENTER;
			constraints.insets      = new Insets(0, 5, 0, 0);
			this.add(groupingElementChooser, constraints);
			this.addPaneForAlignment(groupingElementChooser);
			addHelpTopicId(groupingElementChooser, "mapping.eisOneToMany.foreignKeyGroupingElement");
			
			// Field Pairs label
			JLabel fieldPairsLabel = 
				this.buildFieldPairsLabel();
			constraints.gridx       = 0;
			constraints.gridy       = 1;
			constraints.gridwidth   = 2;
			constraints.gridheight  = 1;
			constraints.weightx     = 0;
			constraints.weighty     = 0;
			constraints.fill        = GridBagConstraints.NONE;
			constraints.anchor      = GridBagConstraints.LINE_START;
			constraints.insets      = new Insets(5, 0, 0, 0);
			this.add(fieldPairsLabel, constraints);
			this.addAlignLeft(fieldPairsLabel);
			addHelpTopicId(fieldPairsLabel, "mapping.eisOneToMany.fieldPairs");

			// Field Pairs table
			AbstractPanel fieldPairsPanel = 
				this.buildFieldPairsPanel();
			constraints.gridx       = 0;
			constraints.gridy       = 2;
			constraints.gridwidth   = 2;
			constraints.gridheight  = 1;
			constraints.weightx     = 1;
			constraints.weighty     = 1;
			constraints.fill        = GridBagConstraints.BOTH;
			constraints.anchor      = GridBagConstraints.CENTER;
			constraints.insets      = new Insets(1, 0, 0, 0);
			this.add(fieldPairsPanel, constraints);
			this.addPaneForAlignment(fieldPairsPanel);
			addHelpTopicId(fieldPairsPanel, "mapping.eisOneToMany.fieldPairs");
		}
		
		private JLabel buildGroupingElementLabel() {
			JLabel label = this.buildLabel("FOREIGN_KEYS_GROUPING_ELEMENT_CHOOSER");
			this.addHelpTopicId(label, "mapping.eisOneToMany.foreignKeyGroupingElement");
			return label;
		}	
		
		private XpathChooser buildGroupingElementXpathChooser() {
			XpathChooser chooser = 
				new XpathChooser(
					this.getWorkbenchContextHolder(), 
					this.buildGroupingElementXpathHolder()
				);
			this.addHelpTopicId(chooser, "mapping.eisOneToMany.foreignKeyGroupingElement");
			return chooser;
		}

		private ValueModel buildGroupingElementXpathHolder() {
			return new PropertyAspectAdapter(getSubjectHolder()) {
				protected Object getValueFromSubject() {
					return ((MWEisOneToManyMapping) this.subject).getForeignKeyGroupingElement();
				}
			};
		}
		
		private JLabel buildFieldPairsLabel() {
			JLabel label = this.buildLabel("FIELD_PAIR_TABLE");
			this.addHelpTopicId(label, "mapping.eisOneToMany.fieldPairs");
			return label;
		}
		
		private AbstractPanel buildFieldPairsPanel() {
			AbstractPanel panel = 
				new EisReferenceMappingFieldPairsPanel(
					EisOneToManyGeneralPropertiesPage.this.getSelectionHolder(), 
					this.getWorkbenchContextHolder()
				);
			this.addHelpTopicId(panel, "mapping.eisOneToMany.foreignKeys");
			return panel;
		}
		
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			
			for (int i = this.getComponentCount() - 1; i >= 0; i --) {
				this.getComponent(i).setEnabled(enabled);
			}
		}
	}
}
