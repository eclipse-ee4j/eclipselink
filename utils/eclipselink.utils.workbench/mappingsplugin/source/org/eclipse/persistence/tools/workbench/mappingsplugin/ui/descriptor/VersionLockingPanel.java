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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public abstract class VersionLockingPanel extends AbstractSubjectPanel {

	private PropertyValueModel optimisticVersionLockingPolicyTypeModel;
	private JPanel optimisticLockingFieldChooser;

	protected VersionLockingPanel(ValueModel lockingPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(lockingPolicyHolder, contextHolder);
	}

	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);
		this.optimisticVersionLockingPolicyTypeModel = buildOptimisticVersionLockingPolicyTypeValueHolder();
	}


	private PropertyValueModel buildOptimisticVersionLockingPolicyTypeValueHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(),
				MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorLockingPolicy) subject).getOptimisticVersionLockingType();
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorLockingPolicy) subject).setOptimisticVersionLockingType((String) value);
			}
		}; 
	}

	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Action:	Create the database field chooser
		this.optimisticLockingFieldChooser = buildVersionLockingFieldChooser();
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 10, 0, 0);

		add(this.optimisticLockingFieldChooser, constraints);

		// Action:	Create the version locking
		JRadioButton versionLocking = buildUseVersionLockingButton();

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(10, 5, 0, 0);

		add(versionLocking, constraints);


		// Action:	Create the timestamp locking
		JPanel versionTimestampLockingPanel = buildOptimisticVersionTimestampLockingPanel();
		JRadioButton timestampLocking = buildUseTimestampButton();

		GroupBox versionLockingBox = new GroupBox(timestampLocking, versionTimestampLockingPanel);


		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(versionLockingBox, constraints);

		// Action:	Create the non-cached locking check box
		JCheckBox versionCache = buildStoreVersionInCacheCheckBox();

		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);

		add(versionCache, constraints);
		
		buildVersionLockingPanelEnabler(
				new Component[] 
					    {this.optimisticLockingFieldChooser, 
						versionLocking, 
						timestampLocking,
						versionCache});
	}

	protected abstract JPanel buildVersionLockingFieldChooser();

	
	private JRadioButton buildUseVersionLockingButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_VERSION_LOCKING",
					new RadioButtonModelAdapter(
						this.optimisticVersionLockingPolicyTypeModel, 
						MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_VERSION
					)
				);

	}


	private JRadioButton buildUseTimestampButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_TIMESTAMP_LOCKING",
					new RadioButtonModelAdapter(
						this.optimisticVersionLockingPolicyTypeModel, 
						MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP
					)
				);
	}

	// ********* store version in cache ***********
	
	private JCheckBox buildStoreVersionInCacheCheckBox() {
		return buildCheckBox("LOCKING_POLICY_STORE_VERSION", buildStoreVersionInCacheCheckBoxModel());
	}
	
	private ButtonModel buildStoreVersionInCacheCheckBoxModel() {
		return new CheckBoxModelAdapter(buildStoreVersionInCacheModel());
	}
	
	private PropertyValueModel buildStoreVersionInCacheModel() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWDescriptorLockingPolicy.STORE_IN_CACHE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWDescriptorLockingPolicy) subject).shouldStoreVersionInCache());
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorLockingPolicy) subject).setStoreInCache(((Boolean) value).booleanValue());
			}
		};
	}	

	
	private ComponentEnabler buildVersionLockingPanelEnabler(Component[] components) {
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(this.optimisticVersionLockingPolicyTypeModel) {
			protected Object transform(Object value) {
				return Boolean.valueOf(value != null);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
	private JPanel buildOptimisticVersionTimestampLockingPanel() {
		return new VersionTimestampLockingPanel(getSubjectHolder(), getWorkbenchContextHolder());
	}

}
