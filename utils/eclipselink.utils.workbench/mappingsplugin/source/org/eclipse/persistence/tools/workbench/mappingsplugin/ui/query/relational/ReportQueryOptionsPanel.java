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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
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
final class ReportQueryOptionsPanel
	extends AbstractPanel 
{
	private PropertyValueModel queryHolder;
	
	ReportQueryOptionsPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		initialize(queryHolder);
		initializeLayout();
	}

	private void initialize(PropertyValueModel queryHolder) {
		this.queryHolder = buildReportQueryHolder(queryHolder);
	}
	
	private PropertyValueModel buildReportQueryHolder(PropertyValueModel queryHolder) {
		return new FilteringPropertyValueModel(queryHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWReportQuery;
			}
		};
	}
	
	protected String helpTopicId() {
		return "descriptor.queries.options";
	}

	protected void initializeLayout() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLabel returnChoiceLabel = buildLabel("RETURN_CHOICE_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(returnChoiceLabel, constraints);
		
		JComboBox returnChoiceComboBox = buildReturnChoiceComboBox();
		addHelpTopicId(returnChoiceComboBox, helpTopicId() + ".returnChoice");
		returnChoiceLabel.setLabelFor(returnChoiceComboBox);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		this.add(returnChoiceComboBox, constraints);
	
		
		JLabel retrievePrimaryKeysLabel = buildLabel("RETRIEVE_PRIMARY_KEYS_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(retrievePrimaryKeysLabel, constraints);
		
		// create the cache statement check box
		JComboBox retrievePrimaryKeysComboBox = buildRetrievePrimaryKeysComboBox();
		addHelpTopicId(retrievePrimaryKeysComboBox, helpTopicId() + ".retrievePrimaryKeys");
		retrievePrimaryKeysLabel.setLabelFor(retrievePrimaryKeysComboBox);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		this.add(retrievePrimaryKeysComboBox, constraints);
		

		JLabel cacheStatementLabel = buildLabel("CACHE_STATMENT_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		this.add(cacheStatementLabel, constraints);
		
		// create the bind parameters combo box
		JComboBox cacheStatementComboBox = buildCacheStatementComboBox();
		addHelpTopicId(cacheStatementComboBox, helpTopicId() + ".cacheStatement");
		cacheStatementLabel.setLabelFor(cacheStatementComboBox);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		this.add(cacheStatementComboBox, constraints);

		
		// create the bind parameters label
		JLabel bindParametersLabel = buildLabel("BIND_PARAMETERS_LABEL");
		constraints.gridx = 0;
		constraints.gridy = 3;
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
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		this.add(bindParametersTriStateComboBox, constraints);
		

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
	
	
	//************ return choice **********
	
	private JComboBox buildReturnChoiceComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(buildReturnChoiceValueModel(), buildReturnChoicePropertyAdapter()));
		comboBox.setRenderer(buildTopLinkOptionModelRenderer());
		
		return comboBox;
	}
	
	private ListCellRenderer buildTopLinkOptionModelRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				//null check for comboBox
				return value == null ? "" : resourceRepository().getString(((TopLinkOption) value).resourceKey());
			}
		};
	}

	private ListValueModel buildReturnChoiceValueModel() {
		return new AbstractReadOnlyListValueModel() {
			public Object getValue() {
				return MWReportQuery.returnChoiceOptions().toplinkOptions();
			}
		};	
	}
	
	private PropertyValueModel buildReturnChoicePropertyAdapter() {
		return new PropertyAspectAdapter(this.queryHolder, MWReportQuery.RETURN_CHOICE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWReportQuery) this.subject).getReturnChoice();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWReportQuery) this.subject).setReturnChoice((MWReportQuery.ReturnChoiceOption) value);
			}	
		};	
	}

	
	//************ retrieve primary keys **********
	
	private JComboBox buildRetrievePrimaryKeysComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(buildRetrievePrimaryKeysValueModel(), buildRetrievePrimaryKeysPropertyAdapter()));
		comboBox.setRenderer(buildTopLinkOptionModelRenderer());
		
		return comboBox;
	}

	private ListValueModel buildRetrievePrimaryKeysValueModel() {
		return new AbstractReadOnlyListValueModel() {
			public Object getValue() {
				return MWReportQuery.retrievePrimaryKeysOptions().toplinkOptions();
			}
		};	
	}
	
	private PropertyValueModel buildRetrievePrimaryKeysPropertyAdapter() {
		return new PropertyAspectAdapter(this.queryHolder, MWReportQuery.RETRIVE_PRIMARY_KEYS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWReportQuery) this.subject).getRetrievePrimaryKeys();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWReportQuery) this.subject).setRetrievePrimaryKeys((MWReportQuery.RetrievePrimaryKeysOption) value);
			}	
		};	
	}

	
	
	//************ cache statement **********

	private JComboBox buildCacheStatementComboBox() {
		return RelationalQueryComponentFactory.buildCacheStatementComboBox(this.queryHolder, resourceRepository());
	}

	//************ bind parameters **********
	
	private JComboBox buildBindParametersComboBox() {
		return RelationalQueryComponentFactory.buildBindParametersComboBox(this.queryHolder, resourceRepository());
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
		ReportQueryAdvancedOptionsDialog dialog = new ReportQueryAdvancedOptionsDialog(getQuery(), this.getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed()) {
			MWReportQuery query = getQuery();
			query.setLocking(dialog.getLocking());
			query.setPrepare(dialog.getPrepare());
			query.setCacheQueryResults(dialog.getCacheQueryResults());

			query.setDistinctState(dialog.getDistinctState());
			query.setQueryTimeout(dialog.getQueryTimeout());
			query.setMaximumRows(dialog.getMaximumRows());
			query.setFirstResult(dialog.getFirstResult());
			query.setExclusiveConnection(dialog.getExclusiveConnection());
		}
	}
	
	
	private MWReportQuery getQuery() {
		return (MWReportQuery) this.queryHolder.getValue();
	}

}
