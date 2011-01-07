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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public final class SchemaComplexTypeChooser extends AbstractPanel {

	/** This is what returns the schema used to populate the dialog. */
	private SchemaValue schemaValue;
	
	/** This holds the currently selected complex type (MWComplexTypeDefintion) */
	private PropertyValueModel complexTypeHolder;
	
	/** This holds the display string of the selected complex type */
	private ComplexTypeDisplayStringValueModel complexTypeDisplayStringHolder;
	
	
	// **************** Constructors ******************************************
	
	public SchemaComplexTypeChooser(WorkbenchContextHolder contextHolder, SchemaValue schemaValue, PropertyValueModel complexTypeHolder, JLabel label) {
		super(contextHolder);
		initialize( schemaValue, complexTypeHolder, label);
	}
	
	
	// **************** Initialization ****************************************
	
	private void initialize(SchemaValue schemaValue, PropertyValueModel complexTypeHolder, JLabel label) {
		this.schemaValue = schemaValue;
		this.complexTypeHolder = complexTypeHolder;
		this.complexTypeDisplayStringHolder = this.buildComplexTypeDisplayStringHolder();
		this.initializeLayout(label);
	}
	
	private ComplexTypeDisplayStringValueModel buildComplexTypeDisplayStringHolder() {
		return new ComplexTypeDisplayStringValueModel(this.complexTypeHolder);
	}
	
	private void initializeLayout(JLabel label) {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// the text field that displays the current selection
		JTextField textField = this.buildTextField();
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		this.add(textField, constraints);
		textField.putClientProperty("labeledBy", label);
		
		// the button that brings up the complex type chooser dialog
		JButton button = this.buildButton();
		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);
		this.addAlignRight(button);
		label.setLabelFor(button);
		SwingComponentFactory.updateButtonAccessibleName(label, button);
		this.add(button, constraints);
	}

	private JTextField buildTextField() {
		JTextField textField = new JTextField(this.buildComplexTypeDisplayStringDocument(), null, 1);
		textField.setEditable(true);
		textField.setEnabled(true);
		return textField;
	}
	
	private DocumentAdapter buildComplexTypeDisplayStringDocument() {
		return new DocumentAdapter(this.complexTypeDisplayStringHolder);
	}
	
	private JButton buildButton() {
		JButton button = this.buildButton("SCHEMA_COMPLEX_TYPE_CHOOSER_BROWSE_BUTTON");
		button.addActionListener(this.buildBrowseAction());
		return button;
	}
	
	private ActionListener buildBrowseAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SchemaComplexTypeChooser.this.promptToComplexTypeElement();
			}
		};
	}
	
	
	// **************** Behavior **********************************************
	
	private void promptToComplexTypeElement() {
		SchemaComplexTypeChooserDialog dialog = new SchemaComplexTypeChooserDialog(getWorkbenchContext(), this.schemaValue.schema(), this.complexTypeHolder);
		dialog.show();
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		for (Iterator stream = CollectionTools.iterator(this.getComponents()); stream.hasNext(); ) {
			((Component) stream.next()).setEnabled(enabled);
		}
	}
	
	
	// **************** Member classes ****************************************
	
	private class ComplexTypeDisplayStringValueModel
		extends PropertyValueModelWrapper
	{
		// **************** Instance variables ********************************
		
		/** The cached display string */
		private String displayString;
		
		/** Listener that will update display string */
		private PropertyChangeListener propertyChangeListener;
		
		/** The namespace we're listening to */
		private MWNamespace namespace;
		
		
		
		// **************** Constructors / Initialization *********************
		
		private ComplexTypeDisplayStringValueModel(PropertyValueModel complexTypeHolder) {
			super(complexTypeHolder);
		}
		
		protected void initialize() {
			super.initialize();
			this.propertyChangeListener = this.buildPropertyChangeListener();
		}
		
		private PropertyChangeListener buildPropertyChangeListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					ComplexTypeDisplayStringValueModel.this.displayStringChanged();
				}
			};
		}
		
		
		// **************** Internal ******************************************
		
		private MWComplexTypeDefinition type() {
			return (MWComplexTypeDefinition) this.valueHolder.getValue();
		}
		
		private void displayStringChanged() {
			Object oldValue = this.getValue();
			this.synchronizeDisplayString();
			Object newValue = this.getValue();
			this.firePropertyChanged(VALUE, oldValue, newValue);	
		}
		
		private void synchronizeDisplayString() {
			if (this.type() == null) {
				this.displayString = resourceRepository().getString("SCHEMA_COMPLEX_TYPE_CHOOSER_NONE_SELECTED_TEXT");
			}
			else {
				this.displayString = this.buildDisplayString(this.type());
			}
		}
		
		private String buildDisplayString(MWComplexTypeDefinition element) {
			return element.qName();
		}
		
		private void engageNewType() {
			if (this.type() != null) {
				this.namespace = this.type().getTargetNamespace();
				this.namespace.addPropertyChangeListener(MWNamespace.NAMESPACE_PREFIX_PROPERTY, this.propertyChangeListener);
			}
		}
		
		private void disengageOldType() {
			if (this.namespace != null) {
				this.namespace.removePropertyChangeListener(MWNamespace.NAMESPACE_PREFIX_PROPERTY, this.propertyChangeListener);
				this.namespace = null;
			}
		}
		
		
		// **************** ValueModel contract *******************************
		
		/**
		 * @see ValueModel#getValue()
		 */
		public Object getValue() {
			return this.displayString;
		}
		
		
		// **************** PropertyValueModel contract ***********************
		
		/**
		 * @see PropertyValueModel#setValue(java.lang.Object)
		 */
		public void setValue(Object value) {
				throw new UnsupportedOperationException();
		}
		
		
		// ********** PropertyValueModelWrapper implementation **********
		
		protected void engageValueHolder() {
			super.engageValueHolder();
			this.engageNewType();
			this.synchronizeDisplayString();
		}
		
		protected void disengageValueHolder() {
			this.disengageOldType();
			super.disengageValueHolder();
			this.synchronizeDisplayString();
		}
		
		/**
		 * @see PropertyValueModelWrapper#valueChanged(java.beans.PropertyChangeEvent)
		 */
		protected void valueChanged(PropertyChangeEvent e) {
			this.disengageOldType();
			this.engageNewType();
			this.displayStringChanged();
		}
	}}
