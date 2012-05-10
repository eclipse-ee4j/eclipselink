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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.TransactionalDescriptorComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public final class RelationalDescriptorQueriesSettingsPage
extends ScrollablePropertiesPage
{

	public RelationalDescriptorQueriesSettingsPage(PropertyValueModel relationalDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(relationalDescriptorNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
	}

	private PropertyValueModel buildQueryManagerHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) subject).getQueryManager();
			}
		};
	}

	protected Component buildPage() {
		setName(resourceRepository().getString("SETTINGS_PANEL_NAME"));

		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Create query timeout panel
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);

		mainPanel.add(buildQueryTimeoutPanel(), constraints);
       
        // Refreshing cache options - panel
        constraints.gridx      = 0;
        constraints.gridy      = 1;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.PAGE_START;
        constraints.insets     = new Insets(5, 5, 5, 5);
        mainPanel.add(TransactionalDescriptorComponentFactory.buildRefreshCachePolicyPanel(getSelectionHolder(), getApplicationContext()), constraints);
        
        
		addHelpTopicId(mainPanel, helpTopicId());
		
		return mainPanel;
	}

	private JPanel buildQueryTimeoutPanel()
	{
		JPanel queryTimeoutPanel = new JPanel(new GridBagLayout());
		queryTimeoutPanel.setBorder(BorderFactory.createTitledBorder(
				resourceRepository().getString("QUERY_TIMEOUT_BORDER_LABEL")));

		GridBagConstraints constraints = new GridBagConstraints();
			
		// build default timeout radio button
		JRadioButton defaultTimeoutRadioButton = buildDefaultQueryTimeOutRadioButton();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 5, 5);
		queryTimeoutPanel.add(defaultTimeoutRadioButton, constraints);

		// build no timeout radio button
		JRadioButton noTimeoutRadioButton = buildNoQueryTimeOutRadioButton();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 5, 5);
		queryTimeoutPanel.add(noTimeoutRadioButton, constraints);
		
		JRadioButton queryTimeoutRadioButton = buildQueryTimeOutRadioButton();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 5, 5);
		queryTimeoutPanel.add(queryTimeoutRadioButton, constraints);
		
		JSpinner queryTimeoutSpinButton = SwingComponentFactory.buildSpinnerNumber(buildQueryTimeoutSpinnerModel());
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 5, 5);
		queryTimeoutPanel.add(queryTimeoutSpinButton, constraints);
		
		JLabel queryTimeoutLabel = new JLabel(resourceRepository().getString("QUERY_TIMEOUT_SECONDS_LABEL"));
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 5, 5);
		queryTimeoutPanel.add(queryTimeoutLabel, constraints);
		
		Component[] queryTimeoutComponents = { queryTimeoutSpinButton, queryTimeoutLabel };
		buildQueryTimeoutSpinnerEnabler(buildQueryTimeoutHolder(), queryTimeoutComponents);

		helpManager().addTopicID(queryTimeoutPanel, "descriptor.queries.timeout");
		return queryTimeoutPanel;
	}

	private JRadioButton buildDefaultQueryTimeOutRadioButton() {
		return buildRadioButton(
					"QUERY_SETTINGS_DEFAULT_TIMEOUT", 
					buildRadioButtonModelAdapter(MWQueryManager.DEFAULT_QUERY_TIMEOUT, true));
	}

	private JRadioButton buildNoQueryTimeOutRadioButton() {
		return buildRadioButton(
					"QUERY_SETTINGS_NO_TIMEOUT", 
					buildRadioButtonModelAdapter(MWQueryManager.QUERY_TIMEOUT_NO_TIMEOUT, false));
	}
	private JRadioButton buildQueryTimeOutRadioButton() {
		return buildRadioButton(
					"QUERY_SETTINGS_TIMEOUT", 
					new RadioButtonModelAdapter(buildQueryTimeoutBooleanHolder(), Boolean.TRUE, false));
	}
	
	private ButtonModel buildRadioButtonModelAdapter(Integer queryTimeOut, boolean defaultValue) {
		return new RadioButtonModelAdapter(buildQueryTimeoutHolder(), queryTimeOut, defaultValue);
	}
	

	private PropertyValueModel buildQueryTimeoutHolder() {
		return new PropertyAspectAdapter(buildQueryManagerHolder(), MWQueryManager.QUERY_TIMEOUT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWQueryManager) subject).getQueryTimeout();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWQueryManager) subject).setQueryTimeout((Integer) value);
			}
		};
	}
	
	private PropertyValueModel buildQueryTimeoutBooleanHolder() {
		return new TransformationPropertyValueModel(buildQueryTimeoutHolder()) {
			protected Object transform(Object value) {
				if (value == null) 
					return null;
				
				return Boolean.valueOf(((Integer) value).intValue() > 0);
			}
			
			protected Object reverseTransform(Object value)
			{
				if (Boolean.TRUE.equals(value)) {
					return ((Integer) valueHolder.getValue()).intValue() > 0 ? valueHolder.getValue() : new Integer(1);
				}
				else
				{
					return MWQueryManager.QUERY_TIMEOUT_NO_TIMEOUT;
				}
			}
		};
	}

	private SpinnerNumberModel buildQueryTimeoutSpinnerModel() {
		SpinnerNumberModel spinnerNumberModel = new NumberSpinnerModelAdapter(buildQueryTimeoutHolder());
		spinnerNumberModel.setMinimum(new Integer(1));
		spinnerNumberModel.setMaximum(new Integer(99999));
		return spinnerNumberModel;	
	}

	private ComponentEnabler buildQueryTimeoutSpinnerEnabler(PropertyValueModel queryTimeoutHolder, Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(queryTimeoutHolder)
		{
			protected Object transform(Object value)
			{
				if (value == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(((Integer) value).intValue() > 0);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	public String helpTopicId() {
		return "descriptor.queries.settings";
	}
}
