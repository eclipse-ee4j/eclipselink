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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;



/**
 *   This is used for the left hand panel of the BasicExpressionPanel
 *   Most of this code is duplicated from QueryableArgumentPanel.  The first argument
 *   can only be a MWQueryableArgument whereas the secondArgument can be a 
 *   MWQueryableArgument, MWQueryParameterArgument, MWLiteralArgument
 * 
 *  I duplicated the code because it was cauing too much confusion and setting different
 *  mnemonics on the same panel was not working at all.
 */
final class FirstArgumentPanel extends AbstractPanel
{
	private PropertyValueModel argumentHolder;
				
	private JTextField queryKeyTextField;
	private JButton editQueryKeyButton;	
	

	FirstArgumentPanel(PropertyValueModel argumentHolder, ValueModel basicExpressionEnablerModel, WorkbenchContextHolder contextHolder) 
	{
		super(contextHolder);
		this.argumentHolder = argumentHolder;
		initializeLayout(basicExpressionEnablerModel);
	}

	private void initializeLayout(ValueModel basicExpressionEnablerModel) {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	
		Collection enablingComponents = new ArrayList();			

		JLabel queryKeyLabel = new JLabel(resourceRepository().getString("QUERY_KEY_LABEL_ON_FIRST_ARGUMENT_PANEL"));
		enablingComponents.add(queryKeyLabel);	
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5,5,0,5);
		this.add(queryKeyLabel, constraints);	
	
		this.queryKeyTextField = buildQueryableTextField();
		enablingComponents.add(queryKeyTextField);	
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.queryKeyTextField, constraints);

		
		this.editQueryKeyButton = new JButton(resourceRepository().getString("QUERY_KEY_EDIT_BUTTON_ON_FIRST_ARGUMENT_PANEL"));
		editQueryKeyButton.setMnemonic(resourceRepository().getMnemonic("QUERY_KEY_EDIT_BUTTON_ON_FIRST_ARGUMENT_PANEL"));
		this.editQueryKeyButton.addActionListener(buildEditQueryKeyAction());
		enablingComponents.add(editQueryKeyButton);	
		new ComponentEnabler(basicExpressionEnablerModel, enablingComponents);
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.editQueryKeyButton, constraints);		
	}
	
	
	// ********* queryable **********

	private JTextField buildQueryableTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(new DocumentAdapter(buildQueryableHolder()));
		textField.setEditable(false);
		return textField;
	}
	
	private PropertyValueModel buildQueryableHolder() {
		return new PropertyAspectAdapter(this.argumentHolder, MWQueryableArgument.QUERYABLE_ARGUMENT_ELEMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWQueryableArgument) subject).displayString();
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
		QueryableEditDialog queryKeyDialog = new QueryableEditDialog((MWQueryableArgument) this.argumentHolder.getValue(), getWorkbenchContext());
		queryKeyDialog.show();
	}
}
