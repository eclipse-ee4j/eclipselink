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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;



/**
 * This is a tab panel found on the Named Queries tab.  
 * It is used to set up basic options on a chosen query.
 * 
 * To set up advanced query options the user chooses the Advanced... button
 */
final class RelationalQueryOptionsPanel extends AbstractPanel 
{
	private PropertyValueModel queryHolder;

	RelationalQueryOptionsPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		initialize(queryHolder);
		initializeLayout();
	}
	
	private void initialize(PropertyValueModel queryHolder) {
		this.queryHolder = buildRelationalQueryHolder(queryHolder);
	}
	
	private PropertyValueModel buildRelationalQueryHolder(PropertyValueModel queryHolder) {
		return new FilteringPropertyValueModel(queryHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWAbstractRelationalReadQuery;
			}
		};
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
		
		// create the cache statement label
		JLabel cacheStatementLabel = buildLabel("CACHE_STATMENT_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(cacheStatementLabel, constraints);
		
		// create the cache statement check box
		JComboBox cacheStatementTriStateComboBox = buildCacheStatementComboBox();
		addHelpTopicId(cacheStatementTriStateComboBox, helpTopicId() + ".cacheState");
		cacheStatementLabel.setLabelFor(cacheStatementTriStateComboBox);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		this.add(cacheStatementTriStateComboBox, constraints);
		
		// create the bind parameters label
		JLabel bindParametersLabel = buildLabel("BIND_PARAMETERS_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(bindParametersLabel, constraints);
		
		// create the bind parameters combo box
		JComboBox bindParametersTriStateComboBox = buildBindParametersComboBox();
		addHelpTopicId(bindParametersTriStateComboBox, helpTopicId() + ".bind");
		bindParametersLabel.setLabelFor(bindParametersTriStateComboBox);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		this.add(bindParametersTriStateComboBox, constraints);
		
		// create the caching options label
		JLabel cachingOptionsLabel = buildLabel("CACHE_USAGE_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(cachingOptionsLabel, constraints);
		
		// create the caching options combo box
		JComboBox cachingOptionsComboBox = buildCachingOptionsComboBox();	
		addHelpTopicId(cachingOptionsComboBox, helpTopicId() + ".cacheUse");	
		cachingOptionsLabel.setLabelFor(cachingOptionsComboBox);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(cachingOptionsComboBox, constraints);
		
		// create the in memory query indirection label
		JLabel inMemoryQueryIndirectionLabel = buildLabel("IN_MEMORY_QUERY_INDIRECTION_LABEL");
		inMemoryQueryIndirectionLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("IN_MEMORY_QUERY_INDIRECTION_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(inMemoryQueryIndirectionLabel, constraints);
		
		// create the in memory query indirection combo box
		JComboBox inMemoryQueryIndirectionComboBox = buildInMemoryQueryIndirectionComboBox();
		addHelpTopicId(inMemoryQueryIndirectionComboBox, helpTopicId() + ".memory");
		inMemoryQueryIndirectionLabel.setLabelFor(inMemoryQueryIndirectionComboBox);
		
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(inMemoryQueryIndirectionComboBox, constraints);
		
		JButton advancedOptionsButton = buildAdvancedOptionsButton();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
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
	
			
			
	//************ cache statement **********
	
	private JComboBox buildCacheStatementComboBox() {
		return RelationalQueryComponentFactory.buildCacheStatementComboBox(this.queryHolder, resourceRepository());
	}


	//************ bind parameters **********
	
	private JComboBox buildBindParametersComboBox() {
		return RelationalQueryComponentFactory.buildBindParametersComboBox(this.queryHolder, resourceRepository());
	}

	
	//************ caching options **********
	
	private JComboBox buildCachingOptionsComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(buildCacheUsageValueModel(), buildCacheUsagePropertyAdapter()));
		comboBox.setRenderer(buildCacheUsageRenderer());
		
		return comboBox;
	}
	
	private ListCellRenderer buildCacheUsageRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				if (value == null) {
					//null check for comboBox
					return "";
				}
				MWAbstractRelationalReadQuery.CacheUsageModel model = (MWAbstractRelationalReadQuery.CacheUsageModel) value;
				return resourceRepository().getString(model.resourceKey());
			}
		};
	}

	private ListValueModel buildCacheUsageValueModel() {
		return new AbstractReadOnlyListValueModel() {
			public Object getValue() {
				return MWAbstractRelationalReadQuery.cacheUsageOptions().toplinkOptions();
			}
		};	
	}
	
	private PropertyValueModel buildCacheUsagePropertyAdapter() {
		return new PropertyAspectAdapter(this.queryHolder, MWAbstractRelationalReadQuery.CACHE_USAGE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWAbstractRelationalReadQuery) this.subject).getCacheUsage();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWAbstractRelationalReadQuery) this.subject).setCacheUsage((MWAbstractRelationalReadQuery.CacheUsageModel) value);
			}	
		};	
	}

	//************ in memory query indirection **********
	
	private JComboBox buildInMemoryQueryIndirectionComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(buildInMemoryQueryIndirectionValueModel(),buildInMemoryQueryIndirectionPolicyPropertyAdapter()));
		comboBox.setRenderer(buildInMemoryQueryIndirectionPolicyRenderer());
		
		return comboBox;
	}
			
	private ListValueModel buildInMemoryQueryIndirectionValueModel() {
		return new AbstractReadOnlyListValueModel() {
			public Object getValue() {
				return MWAbstractRelationalReadQuery.inMemoryQueryIndirectionPolicyOptions().toplinkOptions() ;
			}
		};	
	}
		
	private PropertyValueModel buildInMemoryQueryIndirectionPolicyPropertyAdapter() {
		return new PropertyAspectAdapter(this.queryHolder, MWAbstractRelationalReadQuery.IN_MEMORY_QUERY_INDIRECTION_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWAbstractRelationalReadQuery) this.subject).getInMemoryQueryIndirectionPolicy();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWAbstractRelationalReadQuery) this.subject).setInMemoryQueryIndirectionPolicy((MWAbstractRelationalReadQuery.InMemoryQueryIndirectionPolicyModel) value);
			}
		};	
	}

	private ListCellRenderer buildInMemoryQueryIndirectionPolicyRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				// need null check for combo-box
				return value == null ? "" : resourceRepository().getString(((MWAbstractRelationalReadQuery.InMemoryQueryIndirectionPolicyModel) value).resourceKey());
			}
		};
	}

			
	//************ advanced options **********
	
	private JButton buildAdvancedOptionsButton() {
		JButton button = buildButton("ADVANCED_BUTTON_TEXT");
		button.addActionListener(buildAdvancedOptionsAction());
	
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
		RelationalQueryAdvancedOptionsDialog dialog = new RelationalQueryAdvancedOptionsDialog(getQuery(), this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			MWAbstractRelationalReadQuery query = getQuery();
			query.setMaintainCache(dialog.getMaintainCache());
			query.setLocking(dialog.getLocking());
			query.setPrepare(dialog.getPrepare());
			query.setUseWrapperPolicy(dialog.getUseWrapperPolicy());
			query.setCacheQueryResults(dialog.getCacheQueryResults());
			query.setOuterJoinAllSubclasses(dialog.getOuterJoinAllSubclasses());
			query.setRefreshIdentityMapResult(dialog.getRefreshIdentityMapResult());
			query.setRefreshRemoteIdentityMapResult(dialog.getRefreshRemoteIdentityMapResult());

			query.setDistinctState(dialog.getDistinctState());
			query.setQueryTimeout(dialog.getQueryTimeout());
			query.setMaximumRows(dialog.getMaximumRows());
			query.setFirstResult(dialog.getFirstResult());
			query.setExclusiveConnection(dialog.getExclusiveConnection());
		}
	}
	
	
	private MWAbstractRelationalReadQuery getQuery() {
		return (MWAbstractRelationalReadQuery) this.queryHolder.getValue();
	}

}
