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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;


/**
 * Called when the user chooses to add a Named Query.
 * This class does not require that a query name be unique.
 * Queries can have the same name as long as they have different parameters
 * This case is handled by the rules.
 */
class AddQueryDialog 
	extends AbstractDialog 
	implements CaretListener,
				ActionListener 
{
	
	private boolean supportsReportQuery;
	
	private ButtonGroup typeButtonGroup;
	
	private JTextField nameTextField;
	
	private JRadioButton readObjectQueryRadioButton;
	private JRadioButton readAllQueryRadioButton;
	private JRadioButton reportQueryRadioButton;
	
	protected AddQueryDialog(WorkbenchContext context, boolean supportsReportQuery) {
		super(context);
		this.supportsReportQuery = supportsReportQuery;
	}

	protected Component buildMainPanel() {
		JPanel messagePanel = new JPanel(new GridBagLayout());		
		GridBagConstraints constraints = new GridBagConstraints();
		
		JPanel buttonPanel = buildQueryTypePanel();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 0, 0);
		messagePanel.add(buttonPanel, constraints);

		helpManager().addTopicID(buttonPanel, helpTopicId() + ".type");
		
		// name panel
		JPanel namePanel = buildNamePanel();	
			
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(15, 5, 0, 5);
		messagePanel.add(namePanel, constraints);

		helpManager().addTopicID(namePanel, helpTopicId() + ".name");
		
		getOKAction().setEnabled(false);
		return messagePanel;
	}
	
	protected JPanel buildQueryTypePanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("TYPE_BORDER_LABEL_ON_ADD_QUERY_DIALOG")));
		
		this.typeButtonGroup = new ButtonGroup();

		// Add named query radio button
		this.readObjectQueryRadioButton = new JRadioButton(resourceRepository().getString("READ_OBJECT_RADIOBUTTON_ON_ADD_QUERY_DIALOG"));
		this.readObjectQueryRadioButton.setSelected(true);
		this.readObjectQueryRadioButton.addActionListener(this);
		this.readObjectQueryRadioButton.setActionCommand(MWQuery.READ_OBJECT_QUERY);
		this.typeButtonGroup.add(this.readObjectQueryRadioButton);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 0);
		panel.add(this.readObjectQueryRadioButton, constraints);
		
		// Add finder radio button
		this.readAllQueryRadioButton = new JRadioButton(resourceRepository().getString("READ_ALL_RADIOBUTTON_ON_ADD_QUERY_DIALOG"));
		this.readAllQueryRadioButton.addActionListener(this);
		this.readAllQueryRadioButton.setActionCommand(MWQuery.READ_ALL_QUERY);
		this.typeButtonGroup.add(this.readAllQueryRadioButton);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 0);
		panel.add(this.readAllQueryRadioButton, constraints);
		
		// Add finder radio button
		if (supportsReportQuery()) {
			this.reportQueryRadioButton = new JRadioButton(resourceRepository().getString("REPORT_RADIOBUTTON_ON_ADD_QUERY_DIALOG"));
			this.reportQueryRadioButton.addActionListener(this);
			this.reportQueryRadioButton.setActionCommand(MWQuery.REPORT_QUERY);
			this.typeButtonGroup.add(this.reportQueryRadioButton);
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 0);
			panel.add(this.reportQueryRadioButton, constraints);
		}
		
		
		return panel;
	}
	
	protected void enableQueryTypePanel(boolean enabled) {
		this.readAllQueryRadioButton.setEnabled(enabled);
		this.readObjectQueryRadioButton.setEnabled(enabled);
		if (supportsReportQuery()) {
			this.reportQueryRadioButton.setEnabled(enabled);
		}
	}
	
	protected JPanel buildNamePanel() {
		
		JPanel namePanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLabel nameLabel = new JLabel(resourceRepository().getString("NAME_TEXT_FIELD_LABEL_ON_ADD_QUERY_DIALOG"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 5);
		namePanel.add(nameLabel, constraints);
		
		// Add name text field
		this.nameTextField = new JTextField();
		this.nameTextField.setDocument(new RegexpDocument(RegexpDocument.RE_METHOD));
		this.nameTextField.addCaretListener(this);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 0, 0);
		namePanel.add(this.nameTextField, constraints);
		
		return namePanel;
	}
	public void actionPerformed(ActionEvent ae) {
		getNameTextField().requestFocus();
		validateQuery();
	}
	public void caretUpdate(CaretEvent ce) {
		validateQuery();
	}

	protected String helpTopicId() {
		return "descriptor.queryManager.namedQueries";
	}
	
	protected void initialize() {
		super.initialize();
		setTitle(resourceRepository().getString("ADD_QUERY_DIALOG.title"));
	}
	
	protected Component initialFocusComponent() {
		return this.nameTextField;
	}


	protected JTextField getNameTextField() {
		return this.nameTextField;
	}
	
	protected void setQueryType(String queryType) {
		if (queryType == MWQuery.READ_OBJECT_QUERY) {
			this.readObjectQueryRadioButton.setSelected(true);
		}
		else if (queryType == MWQuery.READ_ALL_QUERY) {
			this.readAllQueryRadioButton.setSelected(true);
		}
		else {
			this.reportQueryRadioButton.setSelected(true);
		}
	}
	
	protected void validateQuery() {
		String queryName = getQueryName();
		boolean enabled = queryName != null;
		enabled &= !queryName.equals("");
		getOKAction().setEnabled(enabled);
	}

	private boolean supportsReportQuery() {
		return this.supportsReportQuery;
	}
	
	public String getQueryName() {
		return this.nameTextField.getText();
	}
	
	public String getQueryType() {
		return this.typeButtonGroup.getSelection().getActionCommand();
	}
}
