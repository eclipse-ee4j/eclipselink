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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;



/**
 * This panel is used on the ExpressionBuilderDialog
 * If the user choose to create a query key argument for their expression, this panel is shown.
 * They must then select the necessary MWQueryable by pressing the Edit button
 * 
 * If editing this panel, make sure to look at FirstArgumentPanel as well.  It is
 * basically a copy of this information
 */
final class QueryableArgumentPanel 
	extends ArgumentPanel
{
		
	private JTextField queryKeyTextField;
	private JButton editQueryKeyButton;
	
	QueryableArgumentPanel(PropertyValueModel argumentHolder, WorkbenchContextHolder contextHolder, Collection enablingComponents) {
		super(argumentHolder, contextHolder);
		initialize(enablingComponents);
	}	
	
	protected MWQueryableArgument getQueryableArgument() {
		return (MWQueryableArgument) getArgument();
	}

	private void initialize(Collection enablingComponents) {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		this.queryKeyTextField = buildQueryableTextField();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.SOUTH;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.queryKeyTextField, constraints);

		
		this.editQueryKeyButton = new JButton(resourceRepository().getString("QUERY_KEY_EDIT_BUTTON_ON_SECOND_ARGUMENT_PANEL"));
		editQueryKeyButton.setMnemonic(resourceRepository().getMnemonic("QUERY_KEY_EDIT_BUTTON_ON_SECOND_ARGUMENT_PANEL"));
		this.editQueryKeyButton.addActionListener(buildEditQueryKeyAction());
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.WEST;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.editQueryKeyButton, constraints);
		
		enablingComponents.add(this.editQueryKeyButton);
	}
	
	
	// ********* queryable **********

	private JTextField buildQueryableTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(new DocumentAdapter(buildQueryableHolder()));
		textField.setEditable(false);
		return textField;
	}
	
	private PropertyValueModel buildQueryableHolder() {
		return new PropertyAspectAdapter(getArgumentHolder(), MWQueryableArgument.QUERYABLE_ARGUMENT_ELEMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWQueryableArgument) subject).displayString();
			}
		};
	}

    protected PropertyValueModel buildQueryArgumentHolder(PropertyValueModel argumentHolder) {
		return new FilteringPropertyValueModel(argumentHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWQueryableArgument;
			}
		};    
	}

	private ActionListener buildEditQueryKeyAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				editQueryKey();
			}
		};
	}

	private void editQueryKey() {
		QueryableEditDialog queryKeyDialog = new QueryableEditDialog(getQueryableArgument(), getWorkbenchContext());
		queryKeyDialog.show();
	}
}
