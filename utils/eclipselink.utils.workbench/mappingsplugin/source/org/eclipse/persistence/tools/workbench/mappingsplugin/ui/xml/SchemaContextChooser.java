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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


/**
 * This panel looks like a ListChooser, but the button launches a
 * schema context chooser dialog. 
 */
public final class SchemaContextChooser 
	extends AbstractPanel
{
	/** This is what returns the schemas used to populate the dialog. */
	private SchemaRepositoryValue schemaRepositoryValue;
	
	/** This holds the currently selected schema component (MWSchemaContextComponent) */
	private PropertyValueModel schemaContextComponentHolder;
	
	/** This holds the display string of the selected schema context */
	private SchemaContextDisplayStringValueModel schemaContextDisplayStringHolder;
	
	
	// **************** Constructors ******************************************
	
	private SchemaContextChooser(WorkbenchContextHolder contextHolder) {
		super(contextHolder);
	}
	
	public SchemaContextChooser(WorkbenchContextHolder contextHolder, SchemaRepositoryValue schemaRepository, PropertyValueModel schemaContextHolder, JLabel label) {
		this(contextHolder);
		initialize(schemaRepository, schemaContextHolder, label);
	}
	
	
	// **************** Initialization ****************************************
	
	private void initialize(SchemaRepositoryValue schemaRepository, PropertyValueModel schemaContextComponentHolder, JLabel label) {
		this.schemaRepositoryValue = schemaRepository;
		this.schemaContextComponentHolder = schemaContextComponentHolder;
		this.schemaContextDisplayStringHolder = this.buildSchemaContextDisplayStringHolder();
		this.initializeLayout(label);
	}
	
	private SchemaContextDisplayStringValueModel buildSchemaContextDisplayStringHolder() {
		return new SchemaContextDisplayStringValueModel(this.schemaContextComponentHolder);
	}
	
	private void initializeLayout(JLabel label) {
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		// the (read-only) text field that displays the current selection
		JTextField textField = this.buildTextField();
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		this.add(textField, constraints);
		textField.putClientProperty("labeledBy", label);
		
		// the button that brings up the schema context chooser dialog
		JButton button = this.buildButton();
		constraints.gridx 		= 1;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 5, 0, 0);
		this.addAlignRight(button);
		label.setLabelFor(button);
		SwingComponentFactory.updateButtonAccessibleName(label, button);
		this.add(button, constraints);
	}
	
	private JTextField buildTextField() {
		JTextField textField = new JTextField(this.buildSchemaContextDisplayStringDocument(), null, 1);
		textField.setEditable(false);
		textField.setEnabled(true);
		return textField;
	}
	
	private DocumentAdapter buildSchemaContextDisplayStringDocument() {
		return new DocumentAdapter(this.schemaContextDisplayStringHolder);
	}
	
	private JButton buildButton() {
		JButton button = new JButton(this.resourceRepository().getString("SCHEMA_CONTEXT_CHOOSER_BROWSE_BUTTON"));
		button.addActionListener(this.buildBrowseAction());
		return button;
	}
	
	private ActionListener buildBrowseAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SchemaContextChooser.this.promptToSelectSchemaContext();
			}
		};
	}
	
	
	// **************** Behavior **********************************************
	
	private void promptToSelectSchemaContext() {
		SchemaContextChooserDialog dialog = new SchemaContextChooserDialog(this.getWorkbenchContext(), this.schemaRepositoryValue, this.schemaContextComponentHolder);
		dialog.show();
	}
	
	
	// **************** Member classes ****************************************
	
	private class SchemaContextDisplayStringValueModel
		extends PropertyValueModelWrapper
	{
		// **************** Instance variables ********************************
		
		/** The cached display string */
		private String displayString;
		
		/** Generic istener that will update display string */
		private PropertyChangeListener speakerListener;
		
		/** Used to keep track of what objects we're listening to */
		private Set activeSpeakers;
		
		
		
		// **************** Constructors / Initialization *********************
		
		private SchemaContextDisplayStringValueModel(PropertyValueModel schemaContextComponentHolder) {
			super(schemaContextComponentHolder);
		}
		
		protected void initialize() {
			super.initialize();
			this.speakerListener = this.buildSpeakerListener();
			this.activeSpeakers = new HashSet();
		}
		
		private PropertyChangeListener buildSpeakerListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					SchemaContextDisplayStringValueModel.this.displayStringChanged();
				}
			};
		}
		
		
		// **************** Internal ******************************************
		
		private MWSchemaContextComponent schemaContextComponent() {
			return (MWSchemaContextComponent) this.valueHolder.getValue();
		}
		
		private void displayStringChanged() {
			Object oldValue = this.getValue();
			this.synchronizeDisplayString();
			Object newValue = this.getValue();
			this.firePropertyChanged(VALUE, oldValue, newValue);	
		}
		
		private void synchronizeDisplayString() {
			this.displayString = 
				SchemaContextComponentDisplayer.displayString(
					resourceRepository(),
					this.schemaContextComponent()
				);
		}
		
		private void engageNewSchemaContext() {
			if (this.schemaContextComponent() != null) {
				this.engageSpeaker(this.schemaContextComponent().getSchema());
				
				for (Iterator stream = this.schemaContextComponent().namedComponentChain(); stream.hasNext(); ) {
					this.engageSpeaker(((MWNamedSchemaComponent) stream.next()).getTargetNamespace());
				}
			}
		}
		
		private void engageSpeaker(MWModel speaker) {
			this.activeSpeakers.add(speaker);
			speaker.addPropertyChangeListener(this.speakerListener);
		}
		
		private void disengageOldSchemaContext() {
			for (Iterator stream = this.activeSpeakers.iterator(); stream.hasNext(); ) {
				((MWModel) stream.next()).removePropertyChangeListener(this.speakerListener);
			}
			
			this.activeSpeakers.clear();
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
			this.engageNewSchemaContext();
			this.synchronizeDisplayString();
		}
		
		protected void disengageValueHolder() {
			this.disengageOldSchemaContext();
			super.disengageValueHolder();
			this.synchronizeDisplayString();
		}
		
		/**
		 * @see PropertyValueModelWrapper#valueChanged(java.beans.PropertyChangeEvent)
		 */
		protected void valueChanged(PropertyChangeEvent e) {
			this.disengageOldSchemaContext();
			this.engageNewSchemaContext();
			this.displayStringChanged();
		}
	}
}
