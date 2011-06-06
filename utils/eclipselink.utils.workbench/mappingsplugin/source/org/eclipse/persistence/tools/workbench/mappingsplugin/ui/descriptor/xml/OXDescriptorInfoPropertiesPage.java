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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaComplexTypeChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaContextChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaRootElementChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


final class OXDescriptorInfoPropertiesPage 
	extends ScrollablePropertiesPage 
{
	
	
	// **************** Constructors ******************************************
	
	OXDescriptorInfoPropertiesPage(PropertyValueModel eisDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(eisDescriptorNodeHolder, contextHolder);
	}
	
	private String helpTopicId() {
		return "xmlDescriptor.descriptorInfo";
	}
	
	// **************** Initialization ****************************************
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();
		Insets borderInsets = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		
		// schema context chooser label
		JLabel schemaContextLabel = XmlDescriptorComponentFactory.buildSchemaContextLabel(resourceRepository());
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, borderInsets.left + 5, 0, 0);
		addAlignLeft(schemaContextLabel);
		panel.add(schemaContextLabel, constraints);
		
		// schema context chooser
		SchemaContextChooser schemaContextChooser = XmlDescriptorComponentFactory.buildSchemaContextChooser(getSelectionHolder(), getWorkbenchContextHolder(), schemaContextLabel);
		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, borderInsets.right + 5);
		panel.add(schemaContextChooser, constraints);
		
		// primary keys - panel
		EisPrimaryKeysPanel primaryKeysPanel = this.buildPrimaryKeysPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(primaryKeysPanel, constraints);
		addPaneForAlignment(primaryKeysPanel);

		// any type descriptor check box
		JCheckBox anyTypeDescriptorCheckBox = this.buildAnyTypeDescriptorCheckBox();
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);
		panel.add(anyTypeDescriptorCheckBox, constraints);
		
		// document root check box - root element panel
		GroupBox groupBox = new GroupBox(this.buildDocumentRootCheckBox(), this.buildRootElementPanel());
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		panel.add(groupBox, constraints);
		
		// preserve document check box
		JCheckBox preserveDocumentCheckBox = this.buildPreserveDocumentCheckBox();
		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);
		panel.add(preserveDocumentCheckBox, constraints);
		
		// comment text field
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx      = 0;
		constraints.gridy      = 5;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "descriptor.general.comment");

		addHelpTopicId(panel, helpTopicId());
		return panel;
	}
	
	
	// **************** AnyType descriptor ************************************
	
	private JCheckBox buildAnyTypeDescriptorCheckBox() {
		JCheckBox checkBox = buildCheckBox("ANY_TYPE_DESCRIPTOR_CHECK_BOX", new CheckBoxModelAdapter(this.buildAnyTypeDescriptorValue()));
		this.addHelpTopicId(checkBox, this.helpTopicId() + ".anyTypeDescriptor");
		return checkBox;
	}
	
	private PropertyValueModel buildAnyTypeDescriptorValue() {
		 return new PropertyAspectAdapter(this.getSelectionHolder(), MWOXDescriptor.ANY_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWOXDescriptor) subject).isAnyTypeDescriptor());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWOXDescriptor) subject).setAnyTypeDescriptor(((Boolean) value).booleanValue());
			}
		};
	}
	
	
	// **************** Root Policy *******************************************
	
	private JCheckBox buildDocumentRootCheckBox() {
		JCheckBox checkBox = buildCheckBox("DOCUMENT_ROOT_CHECK_BOX", new CheckBoxModelAdapter(this.buildDocumentRootHolder()));
		addHelpTopicId(checkBox, helpTopicId() + ".documentRoot");
		return checkBox;
	}
	
	private PropertyValueModel buildDocumentRootHolder() {
		 return new PropertyAspectAdapter(getSelectionHolder(), MWOXDescriptor.ROOT_DESCRIPTOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWOXDescriptor) subject).isRootDescriptor());
			}

			protected void setValueOnSubject(Object value) {
				((MWOXDescriptor) subject).setRootDescriptor(((Boolean) value).booleanValue());
			}
		};
	}
	
	protected JPanel buildRootElementPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());
	
		JLabel label = this.buildDefaultRootElementLabel();
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		addAlignLeft(label);
		panel.add(label, constraints);
		
		SchemaRootElementChooser chooser = this.buildDefaultRootElementChooser(label);
		label.setLabelFor(chooser);
		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);
		panel.add(chooser, constraints);
		addPaneForAlignment(chooser);
		
		JLabel typeLabel = this.buildDefaultRootElementTypeLabel();
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		addAlignLeft(typeLabel);
		panel.add(typeLabel, constraints);
		
		SchemaComplexTypeChooser typeChooser = this.buildDefaultRootElementTypeChooser(typeLabel);
		typeLabel.setLabelFor(typeChooser);
		constraints.gridx      = 1;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);
		panel.add(typeChooser, constraints);
		addPaneForAlignment(typeChooser);

		addHelpTopicId(panel, this.helpTopicId() + ".rootElement");
		
		return panel;
	}
	
	protected JLabel buildDefaultRootElementLabel() {
		JLabel label = XmlDescriptorComponentFactory.buildDefaultRootElementLabel(resourceRepository());
		this.buildDocumentRootHolder().addPropertyChangeListener(PropertyValueModel.VALUE, this.buildDocumentRootListener(label));
		return label;
	}
	
	protected SchemaRootElementChooser buildDefaultRootElementChooser(JLabel label) {
		SchemaRootElementChooser chooser = XmlDescriptorComponentFactory.buildDefaultRootElementChooser(getSelectionHolder(), getWorkbenchContextHolder(), label);
		this.buildDocumentRootHolder().addPropertyChangeListener(PropertyValueModel.VALUE, this.buildDocumentRootListener(chooser));
		return chooser;
	}
	
	protected JLabel buildDefaultRootElementTypeLabel() {
		JLabel label = XmlDescriptorComponentFactory.buildDefaultRootElementTypeLabel(resourceRepository());
		this.buildDocumentRootHolder().addPropertyChangeListener(PropertyValueModel.VALUE, this.buildDocumentRootListener(label));
		return label;
	}
	
	protected SchemaComplexTypeChooser buildDefaultRootElementTypeChooser(JLabel label) {
		SchemaComplexTypeChooser chooser = XmlDescriptorComponentFactory.buildDefaultRootElementTypeChooser(getSelectionHolder(), getWorkbenchContextHolder(), label);
		this.buildDocumentRootHolder().addPropertyChangeListener(PropertyValueModel.VALUE, this.buildDocumentRootListener(chooser));
		return chooser;
	}

	private PropertyChangeListener buildDocumentRootListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() == Boolean.TRUE);
			}
		};
	}
	
	
	//***************** Preserve document *********************
	
	private JCheckBox buildPreserveDocumentCheckBox() {
		JCheckBox checkBox = buildCheckBox("PRESERVE_DOCUMENT_CHECK_BOX", new CheckBoxModelAdapter(this.buildPreserveDocumentHolder()));
		addHelpTopicId(checkBox, helpTopicId() + ".preserveDocument");
		return checkBox;
	}
	
	private PropertyValueModel buildPreserveDocumentHolder() {
		 return new PropertyAspectAdapter(getSelectionHolder(), MWOXDescriptor.PRESERVE_DOCUMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWOXDescriptor) subject).isPreserveDocument());
			}

			protected void setValueOnSubject(Object value) {
				((MWOXDescriptor) subject).setPreserveDocument(((Boolean) value).booleanValue());
			}
		};
	}
	
	// **************** Primary keys ******************************************
	
	private EisPrimaryKeysPanel buildPrimaryKeysPanel() {
		return new EisPrimaryKeysPanel(this.getSelectionHolder(), getWorkbenchContextHolder());
	}
	
}
