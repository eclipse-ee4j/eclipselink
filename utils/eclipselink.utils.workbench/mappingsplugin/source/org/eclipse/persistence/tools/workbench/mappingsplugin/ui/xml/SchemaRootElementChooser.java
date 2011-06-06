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
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public final class SchemaRootElementChooser 
	extends AbstractPanel 
{
	/** This is what returns the schema used to populate the dialog. */
	private SchemaValue schemaValue;
	
	/** This holds the currently selected root element (MWElementDeclaration) */
	private PropertyValueModel rootElementHolder;
	
	/** This holds the display string of the selected root element */
	private RootElementDisplayStringValueModel rootElementDisplayStringHolder;
	
	
	// **************** Constructors ******************************************
	
	public SchemaRootElementChooser(WorkbenchContextHolder contextHolder, SchemaValue schemaValue, PropertyValueModel rootElementHolder, JLabel label) {
		super(contextHolder);
		initialize( schemaValue, rootElementHolder, label);
	}
	
	
	// **************** Initialization ****************************************
	
	private void initialize(SchemaValue schemaValue, PropertyValueModel rootElementHolder, JLabel label) {
		this.schemaValue = schemaValue;
		this.rootElementHolder = rootElementHolder;
		this.rootElementDisplayStringHolder = this.buildRootElementDisplayStringHolder();
		this.initializeLayout(label);
	}
	
	private RootElementDisplayStringValueModel buildRootElementDisplayStringHolder() {
		return new RootElementDisplayStringValueModel(this.rootElementHolder);
	}
	
	private void initializeLayout(JLabel label) {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// the (read-only) text field that displays the current selection
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
		
		// the button that brings up the schema context chooser dialog
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
		JTextField textField = new JTextField(this.buildRootElementDisplayStringDocument(), null, 1);
		textField.setEditable(false);
		textField.setEnabled(true);
		return textField;
	}
	
	private DocumentAdapter buildRootElementDisplayStringDocument() {
		return new DocumentAdapter(this.rootElementDisplayStringHolder);
	}
	
	private JButton buildButton() {
		JButton button = this.buildButton("SCHEMA_ROOT_ELEMENT_CHOOSER_BROWSE_BUTTON");
		button.addActionListener(this.buildBrowseAction());
		return button;
	}
	
	private ActionListener buildBrowseAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SchemaRootElementChooser.this.promptToSelectRootElement();
			}
		};
	}
	
	
	// **************** Behavior **********************************************
	
	private void promptToSelectRootElement() {
		SchemaRootElementChooserDialog dialog = new SchemaRootElementChooserDialog(getWorkbenchContext(), this.schemaValue.schema(), this.rootElementHolder);
		dialog.show();
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		for (Iterator stream = CollectionTools.iterator(this.getComponents()); stream.hasNext(); ) {
			((Component) stream.next()).setEnabled(enabled);
		}
	}
	
	
	// **************** Member classes ****************************************
	
	private class RootElementDisplayStringValueModel
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
		
		private RootElementDisplayStringValueModel(PropertyValueModel rootElementHolder) {
			super(rootElementHolder);
		}
		
		protected void initialize() {
			super.initialize();
			this.propertyChangeListener = this.buildPropertyChangeListener();
		}
		
		private PropertyChangeListener buildPropertyChangeListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					RootElementDisplayStringValueModel.this.displayStringChanged();
				}
			};
		}
		
		
		// **************** Internal ******************************************
		
		private MWElementDeclaration element() {
			return (MWElementDeclaration) this.valueHolder.getValue();
		}
		
		private void displayStringChanged() {
			Object oldValue = this.getValue();
			this.synchronizeDisplayString();
			Object newValue = this.getValue();
			this.firePropertyChanged(VALUE, oldValue, newValue);	
		}
		
		private void synchronizeDisplayString() {
			if (this.element() == null) {
				this.displayString = resourceRepository().getString("SCHEMA_ROOT_ELEMENT_CHOOSER_NONE_SELECTED_TEXT");
			}
			else {
				this.displayString = this.buildDisplayString(this.element());
			}
		}
		
		private String buildDisplayString(MWElementDeclaration element) {
			return element.qName();
		}
		
		private void engageNewElement() {
			if (this.element() != null) {
				this.namespace = this.element().getTargetNamespace();
				this.namespace.addPropertyChangeListener(MWNamespace.NAMESPACE_PREFIX_PROPERTY, this.propertyChangeListener);
			}
		}
		
		private void disengageOldElement() {
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
			this.engageNewElement();
			this.synchronizeDisplayString();
		}
		
		protected void disengageValueHolder() {
			this.disengageOldElement();
			super.disengageValueHolder();
			this.synchronizeDisplayString();
		}
		
		/**
		 * @see PropertyValueModelWrapper#valueChanged(java.beans.PropertyChangeEvent)
		 */
		protected void valueChanged(PropertyChangeEvent e) {
			this.disengageOldElement();
			this.engageNewElement();
			this.displayStringChanged();
		}
	}
}
