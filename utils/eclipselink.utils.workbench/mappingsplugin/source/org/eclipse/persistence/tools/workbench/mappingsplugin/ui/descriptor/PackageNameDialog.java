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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;


public final class PackageNameDialog 
	extends AbstractDialog
{
	/** The collection model containing the possible package names and the selected package name */
	protected CollectionValueModel packageNameCollectionModel;
	
	/** Tcontains the selected package name */	
	private PropertyValueModel packageNameHolder;
	
	//initialFocusComponent
	private JComboBox packageComboBox;
	
	// **************** Constructors ******************************************
	
	/**
	 * Default constructor.
	 */
	PackageNameDialog(WorkbenchContext context) {
		this(context, new SimpleCollectionValueModel());
	}
	
	PackageNameDialog(WorkbenchContext context, CollectionValueModel packageNameCollectionModel) {
		this(context, packageNameCollectionModel, new SimplePropertyValueModel(""));
	}

	PackageNameDialog(WorkbenchContext context, CollectionValueModel packageNameCollectionModel, PropertyValueModel packageNameHolder) {
		super(context);
		this.packageNameCollectionModel = packageNameCollectionModel;
		this.packageNameHolder = packageNameHolder;
	}
	

	// **************** Initialization ****************************************
	
	protected String helpTopicId() {
		return "dialog.createNewClass";
	}

	protected void initialize() {
		super.initialize();
				
		this.setTitle(resourceRepository().getString("MOVE_DESCRIPTOR_DIALOG.title"));
		//this.setPreferredSize(400, 150);
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Add package name label
		JLabel packageLabel = SwingComponentFactory.buildLabel("MOVE_DESCRIPTOR_DIALOG_PACKAGE", resourceRepository());
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.WEST;
		constraints.insets 		= new Insets(5, 5, 0, 0);
		panel.add(packageLabel, constraints);
		
		// Add package name combo box
		JComboBox packageComboBox = buildPackageComboBox();
		packageLabel.setLabelFor(packageComboBox);
		constraints.gridx 		= 1;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 5, 0, 5);
		panel.add(packageComboBox, constraints);
		
		return panel;
		
	}
	
	private JComboBox buildPackageComboBox() {
		JComboBox comboBox = new JComboBox();
		comboBox.setEditable(true);
		comboBox.setModel(new ComboBoxModelAdapter(this.packageNameCollectionModel, this.packageNameHolder));
		updatePackageComboBoxEditor(comboBox);

		return comboBox;
	}
		
	
	private void updatePackageComboBoxEditor(JComboBox comboBox) {
		((JTextField) comboBox.getEditor().getEditorComponent()).setDocument(new RegexpDocument(RegexpDocument.RE_PACKAGE));
	}
		
	protected Component initialFocusComponent() {
		return this.packageComboBox;
	}


	

	
	
	// **************** API ***************************************************
	

	public String getPackageName() {
		return (String) this.packageNameHolder.getValue();
	}
	
}
