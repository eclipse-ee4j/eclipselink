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
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.AbstractLockingPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


/**
 * @version 10.1.3
 */
final class TableLockingPolicyPropertiesPage
										extends	 AbstractLockingPolicyPropertiesPage
{

	private PropertyValueModel optimisticLockingPolicyTypeModel;
	
	TableLockingPolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.optimisticLockingPolicyTypeModel = buildOptimisticLockingPolicyTypeValueHolder();
	}
	
	protected Component buildPage() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();

        JRadioButton noneRadioButton = buildUseNoneButton();
        constraints.gridx           = 0;
        constraints.gridy           = 0;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill            = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
        constraints.insets      = new Insets(0, 5, 0, 0);
        mainPanel.add(noneRadioButton, constraints);
        addHelpTopicId(noneRadioButton, getHelpTopicId() + ".none");
      
		JPanel optimisticLockingPanel = buildOptimisticLockingPanel();

		// Create the panel
		GroupBox optimisticBox = new GroupBox(buildUseOptimisticButton(), optimisticLockingPanel);
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		addHelpTopicId(optimisticLockingPanel, getHelpTopicId() + ".optimistic");

		mainPanel.add(optimisticBox, constraints);

		JPanel pessimisticLockingPanel = buildPessimisticLockingPanel();
		buildPessimisticLockingPanelEnabler(pessimisticLockingPanel.getComponents());

		JRadioButton pessimisticButton = buildUsePessimisticButton();
		GroupBox pessimisticBox = new GroupBox(pessimisticButton, pessimisticLockingPanel);

		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		addHelpTopicId(pessimisticLockingPanel, getHelpTopicId() + ".pessimistic");

		mainPanel.add(pessimisticBox, constraints);

		addHelpTopicId(mainPanel, getHelpTopicId());

		return mainPanel;
	}


	private JPanel buildOptimisticFieldsLockingPanel() {
		return new ColumnsLockingPanel(getLockingPolicyHolder(), getWorkbenchContextHolder());
	}

	// --- Optimistic Locking Type ---
	
	private JRadioButton buildOptimisticFieldsLockingTypeButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_OPTIMISTIC_FIELDS",
					new RadioButtonModelAdapter(
						this.optimisticLockingPolicyTypeModel,
						MWTableDescriptorLockingPolicy.OPTIMISTIC_COLUMNS_LOCKING_TYPE
					)
				);
	}
	
	private JPanel buildOptimisticLockingPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());

		// By Version pane
		JPanel versionLockingPanel = buildOptimisticVersionLockingPanel();
		JRadioButton optimisticVersionLockingTypeButton = buildOptimisticVersionLockingTypeButton();

		GroupBox versionLockingBox = new GroupBox(optimisticVersionLockingTypeButton, versionLockingPanel);

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		addHelpTopicId(versionLockingPanel, getHelpTopicId() + ".version");
		panel.add(versionLockingBox, constraints);

		// By Fields pane
		JPanel fieldsLockingPanel = buildOptimisticFieldsLockingPanel();
		JRadioButton optimisticFieldsLockingTypeButton = buildOptimisticFieldsLockingTypeButton();

		GroupBox fieldsLockingBox = new GroupBox(optimisticFieldsLockingTypeButton, fieldsLockingPanel);

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		addHelpTopicId(fieldsLockingPanel, getHelpTopicId() + ".fields");
		panel.add(fieldsLockingBox, constraints);
	
		buildOptimisticTypeRadioButtonEnabler(new Component[] { optimisticVersionLockingTypeButton, optimisticFieldsLockingTypeButton});

		return panel;
	}

	private ComponentEnabler buildOptimisticTypeRadioButtonEnabler(Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(getLockingPolicyTypeHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;
				
				return Boolean.valueOf(MWLockingPolicy.OPTIMISTIC_LOCKING == value);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
	private JPanel buildOptimisticVersionLockingPanel() {
		return new TableVersionLockingPanel(getLockingPolicyHolder(), getWorkbenchContextHolder());
	}

	// --- Version Locking --- 


	
	private JRadioButton buildOptimisticVersionLockingTypeButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_OPTIMISTIC_VERSION",
					new RadioButtonModelAdapter(
							this.optimisticLockingPolicyTypeModel, 
						MWTableDescriptorLockingPolicy.OPTIMISTIC_VERSION_LOCKING_TYPE
					)
				);
	}

	private PropertyValueModel buildOptimisticLockingPolicyTypeValueHolder() {
		return new PropertyAspectAdapter(getLockingPolicyHolder(),
					MWTableDescriptorLockingPolicy.OPTIMISTIC_LOCKING_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptorLockingPolicy) subject).getOptimisticLockingType();
			}

			protected void setValueOnSubject(Object value) {
				((MWTableDescriptorLockingPolicy) subject).setOptimisticLockingType((String) value);
			}
		};
	}


	// --- Field Locking --- 

	
	protected String getHelpTopicId()
	{
		return "descriptor.relational.locking";
	}


}
