/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;


/**
 * very simple dialog used for naming new tables added to the database
 * and renaming existing tables; clients must check the validity of
 * the name
 */
public class TableNameDialog
	extends AbstractDialog
{
	private JLabel messageLabel;

	private JTextField catalogTextField;
	private JTextField schemaTextField;
	private JTextField shortNameTextField;


	// ********** constructors/initialization **********

	TableNameDialog(WorkbenchContext context, String title, String message) {
		this(context, title, message, null);
	}	

	TableNameDialog(WorkbenchContext context, String title, String message, String initialShortName) {
		this(context, title, message, null, null, initialShortName);
	}

	TableNameDialog(WorkbenchContext context, String title, String message, String initialCatalog, String initialSchema, String initialShortName) {
		super(context);
		this.setTitle(title);
		this.messageLabel = new JLabel(message);
		this.catalogTextField = new JTextField(initialCatalog, 30);
		this.schemaTextField = new JTextField(initialSchema, 30);
		this.shortNameTextField = new JTextField(initialShortName, 30);
	}

	protected Component initialFocusComponent() {
		return this.catalogTextField;
	}

	protected String helpTopicId() {
		return "dialog.newTable";
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// message label
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(this.messageLabel, constraints);
		
		// catalog label
		JLabel catalogLabel = SwingComponentFactory.buildLabel("NEW_CATALOG_LABEL", this.resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.add(catalogLabel, constraints);
		
		// catalog text field
		this.helpManager().addTopicID(this.catalogTextField, this.helpTopicId() + ".catalog");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);
		panel.add(this.catalogTextField, constraints);

		catalogLabel.setLabelFor(this.catalogTextField);
		
		// schema label
		JLabel schemaLabel = SwingComponentFactory.buildLabel("NEW_SCHEMA_LABEL", this.resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.add(schemaLabel, constraints);

		// schema text field
		this.helpManager().addTopicID(this.schemaTextField, this.helpTopicId() + ".schema");
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);
		panel.add(this.schemaTextField, constraints);

		schemaLabel.setLabelFor(this.schemaTextField);

		// short name label
		JLabel shortNameLabel = SwingComponentFactory.buildLabel("NEW_TABLE_NAME_LABEL", this.resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.add(shortNameLabel, constraints);

		// short name text field
		this.helpManager().addTopicID(this.shortNameTextField, this.helpTopicId() + ".name");
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);
		panel.add(this.shortNameTextField, constraints);
		
		this.shortNameTextField.getDocument().addDocumentListener(this.buildDocumentListener());

		shortNameLabel.setLabelFor(this.shortNameTextField);

		return panel;
	}


	// ********** behavior **********

	private DocumentListener buildDocumentListener() {
		return new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				// do nothing
			}
			public void insertUpdate(DocumentEvent e) {
				TableNameDialog.this.getOKAction().setEnabled(e.getDocument().getLength() != 0);
			}
			public void removeUpdate(DocumentEvent e) {
				TableNameDialog.this.getOKAction().setEnabled(e.getDocument().getLength() != 0);
			}
		};
	}

	/**
	 * broaden access to method a bit
	 */
	protected Action getOKAction() {
		return super.getOKAction();
	}


	// ********** queries **********

	String catalog() {
		String catalog = this.catalogTextField.getText();
		return (catalog.length() == 0) ? null : catalog;
	}
	
	String schema() {
		String schema = this.schemaTextField.getText();
		return (schema.length() == 0) ? null : schema;
	}

	String shortName() {
		String shortName = this.shortNameTextField.getText();
		return (shortName.length() == 0) ? null : shortName;
	}
	
}
