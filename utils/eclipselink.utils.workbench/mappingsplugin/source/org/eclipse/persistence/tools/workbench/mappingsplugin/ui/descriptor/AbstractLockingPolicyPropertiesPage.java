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
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Vector;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.ComponentVisibilityEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


/**
 * @author bjflanag
 *
 */

public abstract class AbstractLockingPolicyPropertiesPage
		extends ScrollablePropertiesPage
{

	private PropertyValueModel lockingPolicyHolder;
	private PropertyValueModel lockingPolicyTypeHolder;

	public AbstractLockingPolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
		
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.lockingPolicyHolder = buildLockingPolicyHolder();
		this.lockingPolicyTypeHolder = buildLockingPolicyTypeValueHolder();
	}
	
	private PropertyValueModel buildLockingPolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()){
			protected Object getValueFromSubject() {
				return ((MWMappingDescriptor) this.subject).getLockingPolicy();
			}
		};
	}
		
	private PropertyValueModel buildLockingPolicyTypeValueHolder() {
		return new PropertyAspectAdapter(buildLockingPolicyHolder(), MWLockingPolicy.LOCKING_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorLockingPolicy) this.subject).getLockingType();
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorLockingPolicy) this.subject).setLockingType((String) value);
			}
		}; 
	}
	
	protected PropertyValueModel getLockingPolicyHolder() {
		return this.lockingPolicyHolder;
	}
	
	protected PropertyValueModel getLockingPolicyTypeHolder() {
		return this.lockingPolicyTypeHolder;
	}
	
    protected JRadioButton buildUseNoneButton()
    {
        return buildRadioButton(
                    "LOCKING_POLICY_NO_LOCKING",
                    new RadioButtonModelAdapter(
                            getLockingPolicyTypeHolder(), 
                        MWLockingPolicy.NO_LOCKING
                    )
                );
    }

	protected JRadioButton buildUseOptimisticButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_OPTIMISTIC_LOCKING",
					new RadioButtonModelAdapter(
							getLockingPolicyTypeHolder(), 
						MWLockingPolicy.OPTIMISTIC_LOCKING
					)
				);
	}
	
	protected JRadioButton buildUsePessimisticButton()
	{
		return buildRadioButton(
					"LOCKING_POLICY_PESSIMISTIC_LOCKING",
					new RadioButtonModelAdapter(
							getLockingPolicyTypeHolder(), 
						MWLockingPolicy.PESSIMISTIC_LOCKING
					)
				);
	}
	
	protected JPanel buildPessimisticLockingPanel()
	{
		JPanel pessimisticPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		Component waitForLockCheckbox = buildWaitForLockCheckBox(); 
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		pessimisticPanel.add(waitForLockCheckbox, constraints);
		
		return pessimisticPanel;
	}
	
	private PropertyValueModel buildTransactionalPolicyHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder())
		{
			protected Object getValueFromSubject()
			{
				return ((MWMappingDescriptor) this.subject).getTransactionalPolicy();
			}
		};
	}
	
	// ********* wait for lock ***********
	
	private JCheckBox buildWaitForLockCheckBox() {
		return buildCheckBox("LOCKING_POLICY_WAIT_LOCK", buildWaitForLockCheckBoxModel());
	}
	
	private ButtonModel buildWaitForLockCheckBoxModel() {
		return new CheckBoxModelAdapter(buildWaitForLockModel());
	}
	
	private PropertyValueModel buildWaitForLockModel() {
		return new PropertyAspectAdapter(getLockingPolicyHolder(), MWDescriptorLockingPolicy.WAIT_FOR_LOCK_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWDescriptorLockingPolicy) this.subject).shouldWaitForLock());
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorLockingPolicy) this.subject).setWaitForLock(((Boolean) value).booleanValue());
			}
		};
	}
	

	protected ComponentEnabler buildPessimisticLockingPanelEnabler(Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(getLockingPolicyTypeHolder())
		{
			protected Object transform(Object value)
			{
				
				return Boolean.valueOf(MWLockingPolicy.PESSIMISTIC_LOCKING.equals(value));
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}

	
	protected Collection collectComponents(Container parent)
	{
		Vector components = new Vector();
		
		Component[] children = parent.getComponents();
		for (int i=0; i<children.length; i++)
		{
			components.add(children[i]);

			if (children[i] instanceof Container)
			{
				components.addAll(collectComponents((Container) children[i]));
			}
		}
		
		return components;
	}
	
	protected Component[] collectComponentsAsArray(Container parent)
	{
		Collection componentCollection = collectComponents(parent);
		return (Component[]) componentCollection.toArray(new Component[componentCollection.size()]);
	}
}
