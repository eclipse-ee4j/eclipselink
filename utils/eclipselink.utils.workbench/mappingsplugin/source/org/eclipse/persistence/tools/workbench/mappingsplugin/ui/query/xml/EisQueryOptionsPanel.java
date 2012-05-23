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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



/**
 * This is a tab panel found on the Named Queries tab.  
 * It is used to set up basic options on a chosen query.
 * 
 * To set up advanced query options the user chooses the Advanced... button
 */
final class EisQueryOptionsPanel extends AbstractPanel 
{
	private PropertyValueModel queryHolder;

	EisQueryOptionsPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout();
	}
	
	protected String helpTopicId() {
		return "descriptor.queries.options";
	}

	protected void initializeLayout() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		// create the check box panel
		JPanel checkBoxPanel = new JPanel(new GridBagLayout());
		
		// create the refresh identity map results check box
		JCheckBox refreshIdentityMapCheckBox = buildRefreshIdentityMapCheckBox();
		addHelpTopicId(refreshIdentityMapCheckBox, helpTopicId() + ".refresh");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.anchor = GridBagConstraints.WEST;
		checkBoxPanel.add(refreshIdentityMapCheckBox, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(checkBoxPanel, constraints);
	
		addHelpTopicId(checkBoxPanel, helpTopicId() + ".refresh");
		
	
		JButton advancedOptionsButton = buildAdvancedOptionsButton();
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 5, 5, 5);
		this.add(advancedOptionsButton, constraints);

		addHelpTopicId(this, helpTopicId());
	}
	
	
	//************ refresh identity map **********
	
	private JCheckBox buildRefreshIdentityMapCheckBox() {
		return QueryComponentFactory.buildRefreshIdentityMapCheckBox(this.queryHolder, resourceRepository());
	}
	
			
	//************ advanced options **********
	
	private JButton buildAdvancedOptionsButton() {
		JButton button = new JButton(resourceRepository().getString("ADVANCED_BUTTON_TEXT"));
		button.setEnabled(false);
		button.setMnemonic(resourceRepository().getMnemonic("ADVANCED_BUTTON_TEXT"));
		button.addActionListener(buildAdvancedOptionsAction());
		this.queryHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildQueryListener(button));
	
		return button;
	}
	
	private ActionListener buildAdvancedOptionsAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				showAdvancedOptionsDialog();
			}
		};
	}
	
	private void showAdvancedOptionsDialog() {
		EisQueryAdvancedOptionsDialog dialog = new EisQueryAdvancedOptionsDialog(getQuery(), this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			MWAbstractReadQuery query = getQuery();
			query.setMaintainCache(dialog.getMaintainCache());
			query.setLocking(dialog.getLocking());
			query.setUseWrapperPolicy(dialog.getUseWrapperPolicy());
			query.setCacheQueryResults(dialog.getCacheQueryResults());
			query.setRefreshIdentityMapResult(dialog.getRefreshIdentityMapResult());
			query.setRefreshRemoteIdentityMapResult(dialog.getRefreshRemoteIdentityMapResult());

			query.setDistinctState(dialog.getDistinctState());
			query.setQueryTimeout(dialog.getQueryTimeout());
			query.setMaximumRows(dialog.getMaximumRows());
			query.setFirstResult(dialog.getFirstResult());
			query.setExclusiveConnection(dialog.getExclusiveConnection());
		}
	}
	
	
	private MWAbstractReadQuery getQuery() {
		return (MWAbstractReadQuery) this.queryHolder.getValue();
	}
	
	private PropertyChangeListener buildQueryListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(getQuery() != null);	
			}
		};	
	}
}
