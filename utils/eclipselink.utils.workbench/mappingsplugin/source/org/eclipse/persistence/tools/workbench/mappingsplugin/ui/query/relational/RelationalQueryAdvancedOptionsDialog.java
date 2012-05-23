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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryAdvancedOptionsDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;



/**
 * This dialog appears on the NamedQueries tab when a query is chosen and the 'Advanced' button
 * is selelcted on the options tab
 */
final class RelationalQueryAdvancedOptionsDialog extends QueryAdvancedOptionsDialog {

	private ButtonModel outerJoinAllSubclassesButtonModel;

	public RelationalQueryAdvancedOptionsDialog(MWAbstractReadQuery query, WorkbenchContext context) {
		super(query, context);
	}
	

	protected String helpTopicId() {
		return "dialog.relationalQueryAdvancedOptions";
	}
	
	protected Component buildMainPanel() {

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		// create the check box panel
		JPanel checkBoxPanel = new JPanel(new GridBagLayout());
		
			// create the maintain cache check box
			JCheckBox maintainCacheCheckBox = buildMaintainCacheCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(maintainCacheCheckBox, constraints);
			
			// create the use wrapper policy check box
			JCheckBox useWrapperPolicyCheckBox = buildUseWrapperPolicyCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(useWrapperPolicyCheckBox, constraints);
			
			// create the prepare check box
			JCheckBox prepareCheckBox = buildPrepareCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(prepareCheckBox, constraints);
			
			// create the cache query results check box
			JCheckBox cacheQueryResultsCheckBox = buildCacheQueryResultsCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(cacheQueryResultsCheckBox, constraints);
		
			// create the outer join all subclasses check box
			JCheckBox outerJoinAllSubclassesCheckBox = buildOuterJoinAllSubclassesCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(outerJoinAllSubclassesCheckBox, constraints);
		
			// create the refresh remote identity map results check box
			JCheckBox refreshRemoteIdentityMapCheckBox = buildRefreshRemoteIdentityMapCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 5;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(refreshRemoteIdentityMapCheckBox, constraints);
		
			// create the exclusive connection check box
			JCheckBox exclusiveConnectionCheckBox = buildExclusiveConnectionCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 6;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.WEST;
			checkBoxPanel.add(exclusiveConnectionCheckBox, constraints);

		//add the checkBoxPanel
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(0, 5, 0, 5);
		panel.add(checkBoxPanel, constraints);
		
		// create the locking options label
		JLabel lockingOptionsLabel = new JLabel(resourceRepository().getString("PESSIMISTIC_LOCKING_CHECK_BOX_LABEL"));
		lockingOptionsLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("PESSIMISTIC_LOCKING_CHECK_BOX_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		panel.add(lockingOptionsLabel, constraints);
		
		// create the locking options combo box
		JComboBox lockingOptionsComboBox = buildLockingOptionsComboBox();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		lockingOptionsLabel.setLabelFor(lockingOptionsComboBox);
		panel.add(lockingOptionsComboBox, constraints);
		
		// create the distinct state label
		JLabel distinctStateLabel = new JLabel(resourceRepository().getString("DISTINCT_STATE_CHECK_BOX_LABEL"));
		distinctStateLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("DISTINCT_STATE_CHECK_BOX_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		panel.add(distinctStateLabel, constraints);
		
		// create the distinct state combo box
		JComboBox distinctStateComboBox = buildDistinctStateComboBox();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 5);
		distinctStateLabel.setLabelFor(distinctStateComboBox);
		panel.add(distinctStateComboBox, constraints);
		
		// create the query timeout Panel
		JPanel queryTimeoutPanel = new JPanel();
		queryTimeoutPanel.setLayout(new GridBagLayout());
		queryTimeoutPanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("QUERY_TIMEOUT_PANEL.title")));
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 5, 0, 5);
		panel.add(queryTimeoutPanel, constraints);

			PropertyValueModel queryTimeoutHolder = new SimplePropertyValueModel();
			JRadioButton useDescriptorTimeoutRadioButton = buildUseDescriptorTimeoutRadioButton(queryTimeoutHolder);
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			queryTimeoutPanel.add(useDescriptorTimeoutRadioButton, constraints);
			
			JRadioButton noTimeoutRadioButton = buildNoTimeoutRadioButton(queryTimeoutHolder);
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			queryTimeoutPanel.add(noTimeoutRadioButton, constraints);
			
			JRadioButton queryTimeoutRadioButton = buildQueryTimeoutRadioButton(buildQueryTimeoutBooleanHolder(queryTimeoutHolder));
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			queryTimeoutPanel.add(queryTimeoutRadioButton, constraints);
			
			JSpinner queryTimeoutSpinButton = buildQueryTimeoutSpinner(queryTimeoutHolder);
			constraints.gridx = 1;
			constraints.gridy = 2;
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
			constraints.gridy = 2;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 0, 5, 5);
			queryTimeoutPanel.add(queryTimeoutLabel, constraints);
			
			Component[] queryTimeoutComponents = {queryTimeoutSpinButton, queryTimeoutLabel };
			buildQueryTimeoutSpinnerEnabler(queryTimeoutHolder, queryTimeoutComponents);

		helpManager().addTopicID(queryTimeoutPanel, helpTopicId() + ".queryTimeout");
					
		// create the in maximum rows panel
		JPanel maximumRowsPanel = new JPanel();
		maximumRowsPanel.setLayout(new GridBagLayout());
		maximumRowsPanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("MAXIMUM_ROWS_PANEL.title")));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(10, 5, 0, 5);
		panel.add(maximumRowsPanel, constraints);
		
			JCheckBox maximumRowsCheckBox = buildMaximumRowsCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			maximumRowsPanel.add(maximumRowsCheckBox, constraints);
			
			JSpinner maximumRowsSpinButton = buildMaximumRowsSpinner();
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			maximumRowsPanel.add(maximumRowsSpinButton, constraints);

			helpManager().addTopicID(maximumRowsPanel, helpTopicId() + ".maximumRows");
			
			setMaximumRows(maximumRowsCheckBox);

		// create the in first result panel
		JPanel firstResultPanel = new JPanel();
		firstResultPanel.setLayout(new GridBagLayout());
		firstResultPanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("FIRST_RESULT_PANEL.title")));
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(10, 5, 0, 5);
		panel.add(firstResultPanel, constraints);

			JCheckBox firstResultCheckBox = buildFirstResultCheckBox();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			firstResultPanel.add(firstResultCheckBox, constraints);
			
			JSpinner firstResultSpinButton = buildFirstResultSpinner();
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 5, 5);
			firstResultPanel.add(firstResultSpinButton, constraints);

			helpManager().addTopicID(firstResultPanel, helpTopicId() + ".firstResult");
			
			setFirstResult(firstResultCheckBox);

		return panel;
	}
	
	// *********** outer join all subclasses ************
	
	protected JCheckBox buildOuterJoinAllSubclassesCheckBox() {
		this.outerJoinAllSubclassesButtonModel = buildOuterJoinAllSubclassesButtonModel();
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"OUTER_JOIN_ALL_SUBCLASSES_CHECK_BOX",
					this.outerJoinAllSubclassesButtonModel,
					resourceRepository());
		helpManager().addTopicID(checkBox, helpTopicId() + ".outerJoinAllQuery");
		checkBox.setSelected(getQuery().isOuterJoinAllSubclasses());
		return checkBox;
	}
	
	private ButtonModel buildOuterJoinAllSubclassesButtonModel() {
		return new CheckBoxModelAdapter(new SimplePropertyValueModel());
	}
	
	public boolean getOuterJoinAllSubclasses() {
		return this.outerJoinAllSubclassesButtonModel.isSelected();
	}
	


}
