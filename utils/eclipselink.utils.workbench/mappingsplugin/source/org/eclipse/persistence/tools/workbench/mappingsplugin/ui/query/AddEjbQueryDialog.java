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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;


final class AddEjbQueryDialog extends AddQueryDialog {
	
	private JRadioButton namedQueryRadioButton;
	private JRadioButton finderRadioButton;
	private JRadioButton topLinkReservedFinderRadioButton;
	private JRadioButton ejbSelectRadioButton;
	
	private JComboBox nameComboBox;
	
	public AddEjbQueryDialog(WorkbenchContext context, boolean supportsReportQuery) {
		super(context, supportsReportQuery);
	}
	
	protected Component buildMainPanel() {
		JPanel messagePanel = new JPanel(new GridBagLayout());		
		GridBagConstraints constraints = new GridBagConstraints();
		
		JPanel queryVarietyPanel = buildQueryVarietyPanel();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 0, 0);
		messagePanel.add(queryVarietyPanel, constraints);

		helpManager().addTopicID(queryVarietyPanel, helpTopicId() + ".ejb20");

		JPanel queryTypePanel = buildQueryTypePanel();

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 0, 0);
		messagePanel.add(queryTypePanel, constraints);

		helpManager().addTopicID(queryTypePanel, helpTopicId() + ".type");
		
		// name panel
		JPanel namePanel = buildNamePanel();	
			
		constraints.gridx = 0;
		constraints.gridy = 2;
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

	protected JPanel buildNamePanel() {
		JPanel namePanel = super.buildNamePanel();
		
		GridBagConstraints constraints = new GridBagConstraints();
		// Add name combo box
		this.nameComboBox = new JComboBox(MWQueryManager.topLinkReservedFinderNames());
		this.nameComboBox.addActionListener(this);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 0, 0);
		namePanel.add(this.nameComboBox, constraints);
		
		return namePanel;
	}

	private JPanel buildQueryVarietyPanel() {
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("VARIETY_BORDER_LABEL_ON_ADD_QUERY_DIALOG")));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Add named query radio button
		this.namedQueryRadioButton = new JRadioButton(resourceRepository().getString("TOPLINK_NAMED_QUERY_RADIOBUTTON_ON_ADD_QUERY_DIALOG"));
		this.namedQueryRadioButton.addActionListener(this);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 0);
		buttonPanel.add(this.namedQueryRadioButton, constraints);
		
		// Add finder radio button
		this.finderRadioButton = new JRadioButton(resourceRepository().getString("EJB_FINDER_RADIOBUTTON_ON_ADD_QUERY_DIALOG"));
		this.finderRadioButton.addActionListener(this);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 0);
		buttonPanel.add(this.finderRadioButton, constraints);
		
		// Add finder radio button
		this.topLinkReservedFinderRadioButton = new JRadioButton(resourceRepository().getString("TOPLINK_RESERVED_FINDER_RADIOBUTTON_ON_ADD_QUERY_DIALOG"));
		this.topLinkReservedFinderRadioButton.addActionListener(this);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 0);
		buttonPanel.add(this.topLinkReservedFinderRadioButton, constraints);
		
		// Add ejbSelect radio button
		this.ejbSelectRadioButton = new JRadioButton(resourceRepository().getString("EJB_SELECT_TYPE_LABEL_ON_ADD_QUERY_DIALOG"));
		this.ejbSelectRadioButton.addActionListener(this);
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 5, 0);
		buttonPanel.add(this.ejbSelectRadioButton, constraints);
		
		// Add buttons to button group
		ButtonGroup queryVarietyButtonGroup = new ButtonGroup();
		queryVarietyButtonGroup.add(this.namedQueryRadioButton);
		queryVarietyButtonGroup.add(this.finderRadioButton);
		queryVarietyButtonGroup.add(this.topLinkReservedFinderRadioButton);
		queryVarietyButtonGroup.add(this.ejbSelectRadioButton);		

		return buttonPanel;
	}
	
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.namedQueryRadioButton) {
			setVisibleNameEditor(getNameTextField());
			getNameTextField().setText("");
			enableQueryTypePanel(true);
		} 
		else if (source == this.finderRadioButton) {
			setVisibleNameEditor(getNameTextField());
			if (!getNameTextField().getText().startsWith("find")) {
				getNameTextField().setText("find");
			}
			enableQueryTypePanel(true);
		} 
		else if (source == this.topLinkReservedFinderRadioButton) {
			setVisibleNameEditor(this.nameComboBox);
			setQueryTypeForTopLinkReservedFinder();
			enableQueryTypePanel(false);
		}
		else if (source == this.ejbSelectRadioButton) {
			setVisibleNameEditor(getNameTextField());
			if (!getNameTextField().getText().startsWith("ejbSelect")) {
				getNameTextField().setText("ejbSelect");
			}
			enableQueryTypePanel(true);
		}
		else if (source == this.nameComboBox) {
			setQueryTypeForTopLinkReservedFinder();
		}
		super.actionPerformed(ae);
	}
	
	private void setVisibleNameEditor(JComponent visibleComponent) {
		if (visibleComponent == getNameTextField()) {
			if (!getNameTextField().isVisible()) {
				getNameTextField().setVisible(true);
			}
			this.nameComboBox.setVisible(false);
		} 
		else if (visibleComponent == this.nameComboBox) {
			if (!this.nameComboBox.isVisible()) {
				this.nameComboBox.setVisible(true);
			}
			getNameTextField().setVisible(false);
		} 
		else {
			throw new RuntimeException(resourceRepository().getString("EITHER_TEXTFIELD_COMBOBOX_SET_VISIBLE_EXCEPTION"));
		}
	}
	
	private void setQueryTypeForTopLinkReservedFinder() {
		if (getQueryName().equals("findByPrimaryKey" )) {
			setQueryType(MWQuery.READ_OBJECT_QUERY);
		} else if (getQueryName().equals("findAll" )) {
			setQueryType(MWQuery.READ_ALL_QUERY);
		} else if (getQueryName().equals("findOneBySql" )) {
			setQueryType(MWQuery.READ_OBJECT_QUERY);
		} else if (getQueryName().equals("findManyBySql" )) {
			setQueryType(MWQuery.READ_ALL_QUERY);
		} else if (getQueryName().equals("findOneByEjbql" )) {
			setQueryType(MWQuery.READ_OBJECT_QUERY);
		} else if (getQueryName().equals("findManyByEjbql" )) {
			setQueryType(MWQuery.READ_ALL_QUERY);
		} else if (getQueryName().equals("findOneByQuery" )) {
			setQueryType(MWQuery.READ_OBJECT_QUERY);
		} else if (getQueryName().equals("findManyByQuery" )) {
			setQueryType(MWQuery.READ_ALL_QUERY);
		}
	}
	protected void prepareToShow() {
		super.prepareToShow();
		this.finderRadioButton.doClick();
	}	
	
	public String getQueryName() {
		if (this.topLinkReservedFinderRadioButton.isSelected())
			return (String) this.nameComboBox.getSelectedItem();
		else
			return getNameTextField().getText();
	}
	
}
