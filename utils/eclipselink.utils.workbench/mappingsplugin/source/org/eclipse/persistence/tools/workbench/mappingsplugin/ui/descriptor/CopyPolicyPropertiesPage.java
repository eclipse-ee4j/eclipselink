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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCopyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class CopyPolicyPropertiesPage extends ScrollablePropertiesPage
{
	private PropertyValueModel copyPolicyHolder;
	// hold on to an istance of this holder since it is used 
	// in several places.
	private PropertyValueModel policyTypeHolder;
	
	final static int EDITOR_WEIGHT = 9;

	CopyPolicyPropertiesPage(PropertyValueModel valueModel, WorkbenchContextHolder contextHolder)
	{
		super(valueModel, contextHolder);
	}

	protected JPanel buildUseCloneMethodPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		// Create the combo box
		ListChooser cloneMethodChooser = buildCloneMethodListChooser();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(cloneMethodChooser, constraints);
		cloneMethodChooser.putClientProperty("labeledBy", new JLabel(" "));
		
		// no descriptive message for now.
//		// Add the message
//		MultiLineLabel cloneMethodText = new MultiLineLabel(WordWrapper.getTextWrapper(), null);
//		cloneMethodText.setText(getWorkbenchContext().getResourceRepository()
//				.getString("theCloneMethodMustBeNonStatic"));
//		constraints.gridx = 0;
//		constraints.gridy = 1;
//		constraints.gridwidth = 1;
//		constraints.gridheight = 1;
//		constraints.weightx = 1;
//		constraints.weighty = 0;
//		constraints.fill = GridBagConstraints.HORIZONTAL;
//		constraints.anchor = GridBagConstraints.CENTER;
//		constraints.insets = new Insets(5, 0, 0, 0);
//		panel.add(cloneMethodText, constraints);

		return panel;
	}
	
	protected String getHelpTopicId()
	{
		return "descriptor.copying";
	}

	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel copyPolicyPanel = new JPanel(new GridBagLayout());
		copyPolicyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Use instantiation policy copy policy
		JRadioButton useInstantiationPolicy = buildUseInstantiationPolicyButton();

		// Use clone method copy policy
		JRadioButton useClone = buildUseCloneMethodPolicyButton();

		// Use method panel
		JPanel useCloneMethodPanel = buildUseCloneMethodPanel();
		buildCloneMethodPaneEnabler(buildPolicyTypeValueHolder(), useCloneMethodPanel.getComponents());

		// Group box
		GroupBox groupBox = new GroupBox
		(
			useInstantiationPolicy,
			useClone,
			useCloneMethodPanel
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		copyPolicyPanel.add(groupBox, constraints);
	
		addHelpTopicId(useCloneMethodPanel, getHelpTopicId() + ".useCloneMethod");
		addHelpTopicId(copyPolicyPanel, getHelpTopicId());
	
		return copyPolicyPanel;
	}

	protected void initialize(PropertyValueModel nodeHolder)
	{
		super.initialize(nodeHolder);
		this.copyPolicyHolder = buildCopyPolicyHolder();
	}

	private PropertyValueModel buildCopyPolicyHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(),
				MWMappingDescriptor.COPY_POLICY_PROPERTY) {
			protected Object getValueFromSubject()
			{
				MWDescriptorPolicy policy = ((MWMappingDescriptor) this.subject).getCopyPolicy();
				return policy.isActive() ? policy : null;
			}
		};
	}

	private JRadioButton buildUseInstantiationPolicyButton()
	{
		JRadioButton radioButton = 
			buildRadioButton(
					"useCopyPolicy",
					new RadioButtonModelAdapter(
							buildPolicyTypeValueHolder(), MWDescriptorCopyPolicy.INSTANTIATION_POLICY));
		return radioButton;
	}

	private JRadioButton buildUseCloneMethodPolicyButton()
	{
		JRadioButton radioButton = 
			buildRadioButton(
					"useCloneMethod",
					new RadioButtonModelAdapter(
							buildPolicyTypeValueHolder(), MWDescriptorCopyPolicy.CLONE));
		return radioButton;
	}

	private PropertyValueModel buildPolicyTypeValueHolder()
	{
		if (this.policyTypeHolder == null)
		{
			this.policyTypeHolder = new PropertyAspectAdapter(this.copyPolicyHolder,
					MWDescriptorCopyPolicy.COPY_POLICY_TYPE_PROPERTY) {
				protected Object getValueFromSubject()
				{
					return ((MWDescriptorCopyPolicy) this.subject).getPolicyType();
				}

				protected void setValueOnSubject(Object value)
				{
					((MWDescriptorCopyPolicy) this.subject).setPolicyType((String) value);
				}
			}; 
		}
		return this.policyTypeHolder; 
	}

	private PropertyValueModel buildClassHolder()
	{
		return new PropertyAspectAdapter(this.copyPolicyHolder) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorCopyPolicy) this.subject).getOwningDescriptor().getMWClass();
			}
		};
	}

	private PropertyValueModel buildCloneMethodSelectionHolder()
	{
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.copyPolicyHolder,
				MWDescriptorCopyPolicy.COPY_METHOD_PROPERTY) {
			protected Object getValueFromSubject(){
				return ((MWDescriptorCopyPolicy) this.subject).getMethod();
			}

			protected void setValueOnSubject(Object value){
				((MWDescriptorCopyPolicy) this.subject).setMethod((MWMethod) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}

	private ListChooser buildCloneMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
				buildCloneMethodComboBoxModel(),
                DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private CachingComboBoxModel buildCloneMethodComboBoxModel()
	{
		return new IndirectComboBoxModel(buildCloneMethodSelectionHolder(), buildClassHolder()) {
			protected ListIterator listValueFromSubject(Object subject) {
				return CopyPolicyPropertiesPage.this.orderedCloneMethodChoices((MWClass) subject);
			}
		};
	}

	ListIterator orderedCloneMethodChoices(MWClass mwClass) {
		return CollectionTools.sort(mwClass.candidateCloneMethods()).listIterator();
	}

	private ComponentEnabler buildCloneMethodPaneEnabler(
			PropertyValueModel useCloneMethodHolder, Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(useCloneMethodHolder)
		{
			protected Object transform(Object value)
			{
				
				return Boolean.valueOf(MWDescriptorCopyPolicy.CLONE.equals(value));
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
}
