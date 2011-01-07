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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;



final class ColumnsLockingPanel extends AbstractSubjectPanel {

	private PropertyValueModel optimisticFieldsLockingPolicyTypeModel;
	
	ColumnsLockingPanel(PropertyValueModel lockingPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(lockingPolicyHolder, contextHolder);
	}

	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);
		this.optimisticFieldsLockingPolicyTypeModel = buildOptimisticFieldsLockingPolicyTypeValueHolder();
	}
	
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		JRadioButton allFieldsLocking = buildAllFieldsLockingButton();

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(allFieldsLocking, constraints);

		JRadioButton changedFieldsLocking = buildChangedFieldsLockingButton();

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(changedFieldsLocking, constraints);
		
		JRadioButton selectedFieldsButton = buildSelectedFieldsLockingButton();

		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(selectedFieldsButton, constraints);

		JPanel addRemoveFieldPanel = buildFieldsLockingFieldAddRemovePanel();

		constraints.gridx			= 0;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, SwingTools.checkBoxIconWidth(), 0, 0);

		add(addRemoveFieldPanel, constraints);

		buildFieldsLockingPanelEnabler(new Component[] {allFieldsLocking, changedFieldsLocking, 
														selectedFieldsButton});

	}
	
	
	private JRadioButton buildAllFieldsLockingButton() {
		return buildRadioButton(
					"LOCKING_POLICY_ALL_FIELDS_LOCKING",
					new RadioButtonModelAdapter(
							this.optimisticFieldsLockingPolicyTypeModel,  
						MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_ALL_COLUMNS
					)
				);
	}

	
	private JRadioButton buildChangedFieldsLockingButton() {
		return buildRadioButton(
					"LOCKING_POLICY_CHANGED_FIELDS_LOCKING",
					new RadioButtonModelAdapter(
							this.optimisticFieldsLockingPolicyTypeModel, 
						MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_CHANGED_COLUMNS
					)
				);
	}
	
	private JRadioButton buildSelectedFieldsLockingButton() {
		return buildRadioButton(
					"LOCKING_POLICY_SELECTED_FIELDS_LOCKING",
					new RadioButtonModelAdapter(
						this.optimisticFieldsLockingPolicyTypeModel, 
						MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_SELECTED_COLUMNS
					)
				);
	}
	
	private PropertyValueModel buildOptimisticFieldsLockingPolicyTypeValueHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(),
					MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_LOCKING_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptorLockingPolicy) subject).getOptimisticColumnsLockingType();
			}

			protected void setValueOnSubject(Object value) {
				((MWTableDescriptorLockingPolicy) subject).setOptimisticColumnsLockingType((String) value);
			}
		};
	}	
	
	private JPanel buildFieldsLockingFieldAddRemovePanel() {
		RelationalLockingColumnsPanel fieldsPanel = 
				new RelationalLockingColumnsPanel((PropertyValueModel) getSubjectHolder(), getWorkbenchContextHolder());

		buildSelectedFieldsPanelEnabler(fieldsPanel);		

		return fieldsPanel;
	}

	private ComponentEnabler buildFieldsLockingPanelEnabler(Component[] components) {
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(this.optimisticFieldsLockingPolicyTypeModel) {
			protected Object transform(Object value) {
				return Boolean.valueOf(value != null);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
	private ComponentEnabler buildSelectedFieldsPanelEnabler(Component component) {
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(this.optimisticFieldsLockingPolicyTypeModel) {
			protected Object transform(Object value) {
				if (value == null) {
					return null;
				}

				return Boolean.valueOf(MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_SELECTED_COLUMNS.equals(value));
			}
		};
		return new ComponentEnabler(booleanHolder, new Component[] {component});
	}

}
