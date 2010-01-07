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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.TransactionalProjectDefaultsPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


/**
 * UI editor for relational project defaults.
 * 
 * @version 10.1.3
 */
public class RelationalProjectDefaultsPropertiesPage extends TransactionalProjectDefaultsPropertiesPage
{

	public RelationalProjectDefaultsPropertiesPage(
		PropertyValueModel projectNodeHolder,
		WorkbenchContextHolder contextHolder)
	{
		super(projectNodeHolder, contextHolder);
	}
	
	protected List buildAdvancedPropertyHolders()
	{
		List holders =  super.buildAdvancedPropertyHolders();
		holders.add(new UIAdvancedPolicyHolder(
				MWRelationalProjectDefaultsPolicy.INTERFACE_ALIAS_POLICY, 
										"RELATIONAL_PROJECT_DEFAULTS_POLICY_INTERFACE_ALIAS_POLICY"));
		holders.add(new UIAdvancedPolicyHolder(MWRelationalProjectDefaultsPolicy.MULTI_TABLE_INFO_POLICY, 
										"RELATIONAL_PROJECT_DEFAULTS_POLICY_MULTI_TABLE_INFO_POLICY"));
		
		return holders;
	}
	
	protected JPanel buildNamedQueriesPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel namedQueriesPanel = new JPanel(new GridBagLayout());
		namedQueriesPanel.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("PROJECT_DEFAULTS_POLICY_NAMED_QUERIES"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5))
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		namedQueriesPanel.add(buildQueriesCacheAllStatementsCheckBox(), constraints);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		namedQueriesPanel.add(buildQueriesBindAllParametersCheckBox(), constraints);

		addHelpTopicId(namedQueriesPanel, helpTopicId() + ".namedQueries");
		return namedQueriesPanel;
	}
	
	private JCheckBox buildQueriesBindAllParametersCheckBox() 
	{
		return buildCheckBox("PROJECT_DEFAULTS_POLICY_BIND_ALL_PARAMETERS", buildQueriesBindAllParametersCheckBoxModel());
	}
	
	private ButtonModel buildQueriesBindAllParametersCheckBoxModel() 
	{
		return new CheckBoxModelAdapter(buildQueriesBindAllParametersValueModel());
	}
	
	private PropertyValueModel buildQueriesBindAllParametersValueModel() 
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWRelationalProjectDefaultsPolicy.QUERIES_BIND_ALL_PARAMETERS_PROPERTY) 
		{
			protected Object getValueFromSubject() 
			{
				return Boolean.valueOf(((MWRelationalProjectDefaultsPolicy) subject).shouldQueriesBindAllParameters());
			}

			protected void setValueOnSubject(Object value) 
			{
				((MWRelationalProjectDefaultsPolicy) subject).setQueriesBindAllParameters(((Boolean) value).booleanValue());
			}
		};
	}
	
	private JCheckBox buildQueriesCacheAllStatementsCheckBox() 
	{
		return buildCheckBox("PROJECT_DEFAULTS_POLICY_CACHE_ALL_STATEMENTS", buildQueriesCacheAllStatementsCheckBoxModel());
	}
	
	private ButtonModel buildQueriesCacheAllStatementsCheckBoxModel() 
	{
		return new CheckBoxModelAdapter(buildQueriesCacheAllStatementsValueModel());
	}
	
	private PropertyValueModel buildQueriesCacheAllStatementsValueModel() 
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWRelationalProjectDefaultsPolicy.QUERIES_CACHE_ALL_STATEMENTS_PROPERTY) 
		{
			protected Object getValueFromSubject() 
			{
				return Boolean.valueOf(((MWRelationalProjectDefaultsPolicy) subject).shouldQueriesCacheAllStatements());
			}

			protected void setValueOnSubject(Object value) 
			{
				((MWRelationalProjectDefaultsPolicy) subject).setQueriesCacheAllStatements(((Boolean) value).booleanValue());
			}
		};
	}
}
