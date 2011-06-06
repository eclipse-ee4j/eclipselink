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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public class VersionTimestampLockingPanel extends AbstractSubjectPanel {

	private PropertyValueModel optimisticVersionLockingPolicyRetrieveTimeModel;
	private PropertyValueModel optimisticVersionLockingPolicyTypeModel;

	protected VersionTimestampLockingPanel(ValueModel lockingPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(lockingPolicyHolder, contextHolder);
	}

	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);
		this.optimisticVersionLockingPolicyRetrieveTimeModel = buildOptimisticVersionLockingPolicyRetrieveTimeValueHolder();
		this.optimisticVersionLockingPolicyTypeModel = buildOptimisticVersionLockingPolicyTypeValueHolder();
	}
	
	protected void initializeLayout() {

		GridBagConstraints constraints = new GridBagConstraints();

		// Action:	Create the version locking
		JRadioButton useServer = buildUseServerTimeButton();

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		addHelpTopicId(useServer, getHelpTopicId() + ".useServer");
		add(useServer, constraints);
		
		// Action:	Create the version locking
		JRadioButton useLocal = buildUseLocalTimeButton();

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		addHelpTopicId(useServer, getHelpTopicId() + ".useLocal");
		add(useLocal, constraints);

		buildVersionTimestampLockingPanelEnabler(new Component[] {useServer, useLocal});

	}

	private PropertyValueModel buildOptimisticVersionLockingPolicyRetrieveTimeValueHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(),
				MWDescriptorLockingPolicy.RETRIEVE_TIME_FROM_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorLockingPolicy) subject).getRetrieveTimeFrom();
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorLockingPolicy) subject).setRetrieveTimeFrom((String) value);
			}
		}; 
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

	private JRadioButton buildUseServerTimeButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_SERVER_TIME",
					new RadioButtonModelAdapter(
						this.optimisticVersionLockingPolicyRetrieveTimeModel, 
						MWDescriptorLockingPolicy.SERVER_TIME
					)
				);

	}


	private JRadioButton buildUseLocalTimeButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_LOCAL_TIME",
					new RadioButtonModelAdapter(
						this.optimisticVersionLockingPolicyRetrieveTimeModel, 
						MWDescriptorLockingPolicy.LOCAL_TIME
					)
				);
	}

	private ComponentEnabler buildVersionTimestampLockingPanelEnabler(Component[] components) {
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(this.optimisticVersionLockingPolicyTypeModel) {
			protected Object transform(Object value) {
				return Boolean.valueOf(value == MWDescriptorLockingPolicy.OPTIMISTIC_VERSION_TIMESTAMP);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}

	protected String getHelpTopicId()
	{
		return "descriptor.relational.locking.optimistic.version.timestamp";
	}

}
