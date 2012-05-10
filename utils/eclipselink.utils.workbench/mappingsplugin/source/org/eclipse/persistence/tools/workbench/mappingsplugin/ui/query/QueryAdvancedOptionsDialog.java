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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public abstract class QueryAdvancedOptionsDialog extends AbstractDialog {

	private MWQuery query;
	
	private ButtonModel cacheQueryResultsButtonModel;
	private ButtonModel exclusiveConnectionButtonModel;
	private ComboBoxModel lockingComboBoxModel;
	private ComboBoxModel distinctStateComboBoxModel;
	private SpinnerNumberModel queryTimeoutSpinnerNumberModel; 
	private ButtonModel maximumRowCheckBoxModel;
	private ButtonModel firstResultCheckBoxModel;
	
	private JSpinner maximumRowsSpinner;//have to hold this to change the enabled state
	private SpinnerNumberModel maximumRowsSpinnerNumberModel;
	
	private JSpinner firstResultSpinner;//have to hold this to change the enabled state
	private SpinnerNumberModel firstResultSpinnerNumberModel;

	private ButtonModel prepareCheckBoxModel;
	
	private ButtonModel maintainCacheCheckBoxModel;
	private ButtonModel useWrapperPolicyCheckBoxModel;
	private ButtonModel refreshRemoteIdentityMapCheckBoxModel;
	private boolean refreshIdentityMapResult;
	
	public QueryAdvancedOptionsDialog(MWQuery query, WorkbenchContext context) {
		super(context);
		this.query = query;
	}

	protected void initialize() {
		super.initialize();
		setTitle(resourceRepository().getString("ADVANCED_QUERY_OPTIONS_DIALOG.title"));
	}

	protected String helpTopicId() {
		return "dialog.advancedQueryOptions";
	}

	
	

	// *********** cache query results ************
	
	protected JCheckBox buildCacheQueryResultsCheckBox() {
		this.cacheQueryResultsButtonModel = buildCacheQueryResultsButtonModel();
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"CACHE_QUERY_RESULTS_CHECK_BOX",
					this.cacheQueryResultsButtonModel,
					resourceRepository());
		helpManager().addTopicID(checkBox, helpTopicId() + ".cacheQuery");
		checkBox.setSelected(this.query.isCacheQueryResults());
		return checkBox;
	}
	
	private ButtonModel buildCacheQueryResultsButtonModel() {
		return new CheckBoxModelAdapter(new SimplePropertyValueModel());
	}
	
	public boolean getCacheQueryResults() {
		return this.cacheQueryResultsButtonModel.isSelected();
	}
	
	
	// *********** exclusive connection ************
	
	protected JCheckBox buildExclusiveConnectionCheckBox() {
		this.exclusiveConnectionButtonModel = buildExclusiveConnectionButtonModel();
		JCheckBox checkBox =
			SwingComponentFactory.buildCheckBox(
					"EXCLUSIVE_CONNECTION_CHECK_BOX", 
					this.exclusiveConnectionButtonModel, 
					resourceRepository());
		helpManager().addTopicID(checkBox, helpTopicId() + ".exclusiveConnection");
		checkBox.setSelected(this.query.isExclusiveConnection());
		
		return checkBox;
	}
	
	private ButtonModel buildExclusiveConnectionButtonModel() {
		return new CheckBoxModelAdapter(new SimplePropertyValueModel());
	}
	
	public boolean getExclusiveConnection() {
		return this.exclusiveConnectionButtonModel.isSelected();
	}

	
	// *********** locking ************

	protected JComboBox buildLockingOptionsComboBox() {
		this.lockingComboBoxModel = new DefaultComboBoxModel(CollectionTools.vector(MWAbstractQuery.lockingOptions().toplinkOptions()));
		JComboBox comboBox = new JComboBox(this.lockingComboBoxModel);
		comboBox.setSelectedItem(this.query.getLocking());
		comboBox.addActionListener(buildLockingAction());
		comboBox.setRenderer(buildTopLinkModelOptionsCellRenderer());
		
		helpManager().addTopicID(comboBox, helpTopicId() + ".pesimisticLock");
		
		return comboBox;
	}
	
	private ListCellRenderer buildTopLinkModelOptionsCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return resourceRepository().getString(((TopLinkOption) value).resourceKey());
			}
		};
	}
			
	private ActionListener buildLockingAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setLocking();
			}
		};
	}
	
	protected void setLocking() {
		MWLockingPolicy lockingPolicy = this.query.getOwningDescriptor().getLockingPolicy();
		
		String lockingMode = ((MWAbstractQuery.LockingModel) this.lockingComboBoxModel.getSelectedItem()).getMWModelOption();
		
		if (lockingMode != MWQuery.NO_LOCK 
				&& (lockingMode== MWQuery.DEFAULT_LOCK_MODE
						&& lockingPolicy.getLockingType() == MWLockingPolicy.PESSIMISTIC_LOCKING)) {
			
			if (!getRefreshIdentityMapResult() || !getRefreshRemoteIdentityMapResult()) {			
				int input = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
												resourceRepository().getString("SET_LOCKING_DIALOG_MESSAGE"),
												resourceRepository().getString("SET_REFRESH_IDENTITY_MAP_RESULTS.title"),
												JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE);
				if (input == JOptionPane.YES_OPTION) {
					this.refreshRemoteIdentityMapCheckBoxModel.setSelected(true);
					this.refreshIdentityMapResult = true;
				}
			}
		}
	}	

	public MWAbstractQuery.LockingModel getLocking() {
		return (MWAbstractQuery.LockingModel) this.lockingComboBoxModel.getSelectedItem();
	}
	

	// *********** distinct state ************

	protected JComboBox buildDistinctStateComboBox() {
		this.distinctStateComboBoxModel = new DefaultComboBoxModel(CollectionTools.vector(MWAbstractQuery.distinctStateOptions().toplinkOptions()));
		JComboBox comboBox = new JComboBox(this.distinctStateComboBoxModel);
		comboBox.setSelectedItem(this.query.getDistinctState());
		comboBox.setRenderer(buildTopLinkModelOptionsCellRenderer());
		helpManager().addTopicID(comboBox, helpTopicId() + ".distinctState");
		
		return comboBox;
	}
	
	public MWAbstractQuery.DistinctStateModel getDistinctState() {
		return (MWAbstractQuery.DistinctStateModel) this.distinctStateComboBoxModel.getSelectedItem();
	}
	
	
	// *********** query timeout ************
	
	protected JRadioButton buildUseDescriptorTimeoutRadioButton(PropertyValueModel booleanHolder) {
		return SwingComponentFactory.buildRadioButton(
					"QUERY_TIMEOUT_USE_DESCRIPTOR_SETTING",
					buildUseDescriptorTimeoutRadioButtonModel(booleanHolder),
					resourceRepository());
	}

	protected JRadioButton buildQueryTimeoutRadioButton(PropertyValueModel booleanHolder) {
		return SwingComponentFactory.buildRadioButton(
				"QUERY_TIMEOUT_TIMEOUT",
				buildQueryTimeoutRadioButtonModel(booleanHolder),
				resourceRepository());
	}
	
	protected JRadioButton buildNoTimeoutRadioButton(PropertyValueModel booleanHolder) {
		return SwingComponentFactory.buildRadioButton(
				"QUERY_TIMEOUT_NO_TIMEOUT",
				buildNoQueryTimeoutRadioButtonModel(booleanHolder),
				resourceRepository());
	}

	private RadioButtonModelAdapter buildUseDescriptorTimeoutRadioButtonModel(PropertyValueModel booleanHolder) {
		return new RadioButtonModelAdapter(booleanHolder, MWAbstractReadQuery.QUERY_TIMEOUT_UNDEFINED);
	}

	private RadioButtonModelAdapter buildQueryTimeoutRadioButtonModel(PropertyValueModel booleanHolder) {
		return new RadioButtonModelAdapter(booleanHolder, Boolean.TRUE);
	}
	
	private RadioButtonModelAdapter buildNoQueryTimeoutRadioButtonModel(PropertyValueModel booleanHolder) {
		return new RadioButtonModelAdapter(booleanHolder, MWAbstractReadQuery.QUERY_TIMEOUT_NO_TIMEOUT);
	}

	protected JSpinner buildQueryTimeoutSpinner(PropertyValueModel queryTimeoutHolder) {
		this.queryTimeoutSpinnerNumberModel = buildQueryTimeoutSpinnerModel(queryTimeoutHolder);
		JSpinner spinner = new JSpinner();
		spinner.setPreferredSize(new Dimension(65, 23));
		spinner.setMinimumSize(new Dimension(65, 23));
		spinner.setMaximumSize(new Dimension(65, 23));		
		spinner.setModel(this.queryTimeoutSpinnerNumberModel);
		this.queryTimeoutSpinnerNumberModel.setValue(this.query.getQueryTimeout());
		
		return spinner;
	}
	
	private SpinnerNumberModel buildQueryTimeoutSpinnerModel(PropertyValueModel queryTimeoutHolder) {
		SpinnerNumberModel spinnerNumberModel = new NumberSpinnerModelAdapter(queryTimeoutHolder);
		spinnerNumberModel.setMinimum(new Integer(1));
		spinnerNumberModel.setMaximum(new Integer(99999));
		return spinnerNumberModel;	
	}
	
	//yeah, this isn't pretty. The model needs to be changed, take a look at the comment in MWQuery
	protected PropertyValueModel buildQueryTimeoutBooleanHolder(PropertyValueModel queryTimeoutHolder) {
		return new TransformationPropertyValueModel(queryTimeoutHolder) {
			protected Object transform(Object value) {
				if (value == null)  {
					return null;
				}
				
				return Boolean.valueOf(((Integer) value).intValue() > 0);
			}
			
			protected Object reverseTransform(Object value) {
				if (Boolean.TRUE.equals(value)) {
					return ((Integer) this.valueHolder.getValue()).intValue() > 0 ? this.valueHolder.getValue() : new Integer(1);
				}
				else {
					return MWAbstractReadQuery.QUERY_TIMEOUT_NO_TIMEOUT;
				}
			}
		};
	}
	protected ComponentEnabler buildQueryTimeoutSpinnerEnabler(PropertyValueModel queryTimeoutHolder, Component[] components) {
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(queryTimeoutHolder) {
			protected Object transform(Object value) {
				return Boolean.valueOf(((Integer) value).intValue() > 0);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}

	
	public Integer getQueryTimeout() {
		return (Integer) this.queryTimeoutSpinnerNumberModel.getValue();	
	}

	// *********** maximum rows ************

	protected JCheckBox buildMaximumRowsCheckBox() {
		this.maximumRowCheckBoxModel = new CheckBoxModelAdapter(new SimplePropertyValueModel());
		
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"NO_MAXIMUM_CHECK_BOX",
					this.maximumRowCheckBoxModel,
					resourceRepository());
		checkBox.setSelected(this.query.getMaximumRows() == 0);
		checkBox.addActionListener(buildMaximumRowsAction(checkBox));
		
		return checkBox;
	}
			
	private ActionListener buildMaximumRowsAction(final JCheckBox checkBox) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setMaximumRows(checkBox);
			}
		};
	}
	
	protected void setMaximumRows(final JCheckBox checkBox) {
		if (checkBox.isSelected()) {
			this.maximumRowsSpinner.setEnabled(false);
			this.maximumRowsSpinnerNumberModel.setMinimum(new Integer(0));
			this.maximumRowsSpinnerNumberModel.setValue(new Integer(0));
		} else {
			this.maximumRowsSpinner.setEnabled(true);
			this.maximumRowsSpinnerNumberModel.setValue(getMaximumRows() == 0 ?
			        new Integer(1) : this.maximumRowsSpinnerNumberModel.getValue());
			this.maximumRowsSpinnerNumberModel.setMinimum(new Integer(1));
		}
	}

	protected JSpinner buildMaximumRowsSpinner() {
		this.maximumRowsSpinnerNumberModel = buildMaximumRowsSpinnerModel();
		this.maximumRowsSpinner = new JSpinner();
		this.maximumRowsSpinner .setEnabled(this.query.getMaximumRows() != 0);
		this.maximumRowsSpinner .setPreferredSize(new Dimension(65, 23));
		this.maximumRowsSpinner .setMinimumSize(new Dimension(65, 23));
		this.maximumRowsSpinner .setMaximumSize(new Dimension(65, 23));		
		this.maximumRowsSpinner .setModel(this.maximumRowsSpinnerNumberModel);
		this.maximumRowsSpinner .setValue(new Integer(this.query.getMaximumRows()));
		return this.maximumRowsSpinner ;
	}
	
	private SpinnerNumberModel buildMaximumRowsSpinnerModel() {
		SpinnerNumberModel spinnerNumberModel = new NumberSpinnerModelAdapter(new SimplePropertyValueModel());
		spinnerNumberModel.setMinimum(new Integer(0));
		spinnerNumberModel.setMaximum(null);
		return spinnerNumberModel;	
	}

	public int getMaximumRows() {
		return ((Integer) this.maximumRowsSpinner.getValue()).intValue();
	}
	
	// *********** first result ************

	protected JCheckBox buildFirstResultCheckBox() {
		this.firstResultCheckBoxModel = new CheckBoxModelAdapter(new SimplePropertyValueModel());
		
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"SET_FIRST_RESULT_CHECK_BOX",
					this.firstResultCheckBoxModel,
					resourceRepository());
		checkBox.setSelected(this.query.getFirstResult() != 0);
		checkBox.addActionListener(buildFirstResultAction(checkBox));
		
		return checkBox;
	}
			
	private ActionListener buildFirstResultAction(final JCheckBox checkBox) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setFirstResult(checkBox);
			}
		};
	}
	
	protected void setFirstResult(final JCheckBox checkBox) {
		if (!checkBox.isSelected()) {
			this.firstResultSpinner.setEnabled(false);
			this.firstResultSpinnerNumberModel.setMinimum(new Integer(0));
			this.firstResultSpinnerNumberModel.setValue(new Integer(0));
		} else {
			this.firstResultSpinner.setEnabled(true);
			this.firstResultSpinnerNumberModel.setValue(getFirstResult() == 0 ?
			        new Integer(1) : this.firstResultSpinnerNumberModel.getValue());
			this.firstResultSpinnerNumberModel.setMinimum(new Integer(1));
		}
	}

	protected JSpinner buildFirstResultSpinner() {
		this.firstResultSpinnerNumberModel = buildFirstResultSpinnerModel();
		this.firstResultSpinner = new JSpinner();
		this.firstResultSpinner.setEnabled(this.query.getFirstResult() != 0);
		this.firstResultSpinner.setPreferredSize(new Dimension(65, 23));
		this.firstResultSpinner.setMinimumSize(new Dimension(65, 23));
		this.firstResultSpinner.setMaximumSize(new Dimension(65, 23));		
		this.firstResultSpinner.setModel(this.firstResultSpinnerNumberModel);
		this.firstResultSpinner.setValue(new Integer(this.query.getFirstResult()));
		return this.firstResultSpinner;
	}
	
	private SpinnerNumberModel buildFirstResultSpinnerModel() {
		SpinnerNumberModel spinnerNumberModel = new NumberSpinnerModelAdapter(new SimplePropertyValueModel());
		spinnerNumberModel.setMinimum(new Integer(0));
		spinnerNumberModel.setMaximum(null);
		return spinnerNumberModel;	
	}

	public int getFirstResult() {
		return ((Integer) this.firstResultSpinner.getValue()).intValue();
	}	
	
	// *********** prepare - only valid for relational queries ************
	
	protected JCheckBox buildPrepareCheckBox() {
		this.prepareCheckBoxModel = new CheckBoxModelAdapter(new SimplePropertyValueModel());
		
		JCheckBox checkBox = SwingComponentFactory.buildCheckBox("PREPARE_CHECK_BOX", this.prepareCheckBoxModel, resourceRepository());
		checkBox.setSelected(((MWRelationalQuery) this.query).isPrepare());
		helpManager().addTopicID(checkBox, helpTopicId() + ".prepare");
		
		return checkBox;
	}
	
	public boolean getPrepare() {
		return this.prepareCheckBoxModel.isSelected();
	}


	
	// *********** maintain cache - only valid for read queries ************
	
	protected JCheckBox buildMaintainCacheCheckBox() {
		this.maintainCacheCheckBoxModel = new CheckBoxModelAdapter(new SimplePropertyValueModel());
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"MAINTAIN_CACHE_CHECK_BOX",
					this.maintainCacheCheckBoxModel,
					resourceRepository());
		checkBox.addActionListener(buildMaintainCacheAction());
		checkBox.setSelected(((MWReadQuery) this.query).isMaintainCache());
		helpManager().addTopicID(checkBox, helpTopicId() + ".maintainCache");
		
		return checkBox;
	}
	

	private ActionListener buildMaintainCacheAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setMaintainCache();
			}
		};
	}

	private void setMaintainCache() {
		if (!getMaintainCache()) {
			if (getRefreshIdentityMapResult() || getRefreshRemoteIdentityMapResult()) {			
				
				int input = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
												resourceRepository().getString("SET_FALSE_MAINTAIN_CACHE_DIALOG_MESSAGE"),
												resourceRepository().getString("SET_REFRESH_IDENTITY_MAP_RESULTS.title"),
												JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE);
				if (input == JOptionPane.YES_OPTION) {
					this.refreshRemoteIdentityMapCheckBoxModel.setSelected(false);
					this.refreshIdentityMapResult = false;
				}
			}
		}
	}

	public boolean getMaintainCache() {
		return this.maintainCacheCheckBoxModel.isSelected();
	}
	
	
	// *********** use wrapper policy - only valid for read queries ************
	
	protected JCheckBox buildUseWrapperPolicyCheckBox() {
		this.useWrapperPolicyCheckBoxModel = new CheckBoxModelAdapter(new SimplePropertyValueModel());
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"USE_WRAPPER_POLICY_CHECK_BOX",
					this.useWrapperPolicyCheckBoxModel,
					resourceRepository());
		
		checkBox.setSelected(((MWReadQuery) this.query).isUseWrapperPolicy());
		helpManager().addTopicID(checkBox, helpTopicId() + ".useWrapper");
		
		return checkBox;
	}
	
	public boolean getUseWrapperPolicy() {
		return this.useWrapperPolicyCheckBoxModel.isSelected();
	}
	
	
	// *********** refresh remote identity map - only valid for read queries ************
	
	protected JCheckBox buildRefreshRemoteIdentityMapCheckBox() {
		this.refreshRemoteIdentityMapCheckBoxModel = new CheckBoxModelAdapter(new SimplePropertyValueModel());
		JCheckBox checkBox = 
			SwingComponentFactory.buildCheckBox(
					"REFRESH_REMOTE_IDENTITY_MAP_RESULTS_CHECK_BOX",
					this.refreshRemoteIdentityMapCheckBoxModel,
					resourceRepository());
		checkBox.setSelected(((MWReadQuery) this.query).isRefreshRemoteIdentityMapResult());
		this.refreshIdentityMapResult = ((MWReadQuery) this.query).isRefreshIdentityMapResult();
		helpManager().addTopicID(checkBox, helpTopicId() + ".refreshRemote");
		
		return checkBox;
	}
	

	public boolean getRefreshRemoteIdentityMapResult() {
		return this.refreshRemoteIdentityMapCheckBoxModel.isSelected();
	}
	
	public boolean getRefreshIdentityMapResult() {
		return this.refreshIdentityMapResult;
	}

	protected MWQuery getQuery() {
		return query;
	}

}
