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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.NameTools;



final class DescriptorGenerationDialog extends AbstractValidatingDialog {
	
	private MWRelationalProject project;
	private JTextField packageNameTextField;
	private JCheckBox generateAccessorsCheckBox; 
	
	public DescriptorGenerationDialog(MWRelationalProject project, WorkbenchContext workbenchContext) {
		super(workbenchContext);
		this.project = project;
		initializeDialog();
	}
		
	public boolean getGenerateAccessors() {
		return this.generateAccessorsCheckBox.isSelected();
	}
		
	public String getPackageName() {
		return this.packageNameTextField.getText();
	}
	
	protected String helpTopicId() {
		return	"dialog.descriptorGeneration";
	}

	private void initializeDialog() {

		// Set help and title string
		String titleString = null;
		titleString = "DescriptorGenerationDialog_class.title";

		setResizable(true);
		setTitle(resourceRepository().getString(titleString));
	}
	
	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		// Create the package name label and text field
		JLabel packageNameLabel = SwingComponentFactory.buildLabel("DescriptorGenerationDialog_packageName", resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(packageNameLabel, constraints);
		
		this.packageNameTextField = new JTextField();
		this.packageNameTextField.setColumns(30);
		Document packageNameDocument = new RegexpDocument(RegexpDocument.RE_PACKAGE);
		packageNameDocument.addDocumentListener(this.buildPackageNameDocumentListener());
        this.packageNameTextField.setDocument(packageNameDocument);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 0);
		mainPanel.add(this.packageNameTextField, constraints);
		packageNameLabel.setLabelFor(this.packageNameTextField);
				
		this.generateAccessorsCheckBox = new JCheckBox(resourceRepository().getString("DescriptorGenerationDialog_generateAccessingMethods"), true);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(this.generateAccessorsCheckBox, constraints);

		// Push everything to the top
		Spacer spacer = new Spacer();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(spacer, constraints);

		return mainPanel;
	}
	
	void showNoRelationshipSupportDialog() {
		JOptionPane.showMessageDialog(
			this, 
			resourceRepository().getString("DescriptorGenerationDialog_noRelationshipSupport.message"),
			resourceRepository().getString("DescriptorGenerationDialog_noRelationshipSupport.title"),
			JOptionPane.WARNING_MESSAGE
		);
	}

	protected DocumentListener buildPackageNameDocumentListener() {
		return new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				DescriptorGenerationDialog.this.packageNameDocumentChanged();
			}
			public void insertUpdate(DocumentEvent e) {
				DescriptorGenerationDialog.this.packageNameDocumentChanged();
			}
			public void changedUpdate(DocumentEvent e) {
				// this probably will never happen...
			}
		};
	}

	void packageNameDocumentChanged() {
		if (this.isVisible()) {
			this.validatePackageName();
		}
	}

	private void validatePackageName() {
		String pkgName = this.packageNameTextField.getText();

		if (this.packageNameIsIllegal(pkgName)) {
			this.setErrorMessageKey("NEW_NAME_DIALOG.ILLEGAL_VALUE");
			return;
		}

		// no problems...
		this.clearErrorMessage();
	}

	private boolean packageNameIsIllegal(String name) {
		String[] segments = name.split("\\.");
		for (int i = 0; i < segments.length; i++) {
			if (NameTools.javaReservedWordsContains(segments[i])) {
				return true;
			}
		}
		return false;
	}

	protected void setErrorMessageKey(String key) {
		super.setErrorMessageKey(key);
		this.getOKAction().setEnabled(false);
	}

	protected void clearErrorMessage() {
		super.clearErrorMessage();
		this.getOKAction().setEnabled(true);
	}

}
