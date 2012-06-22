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
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInstantiationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
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


final class InstantiationPolicyPropertiesPage extends ScrollablePropertiesPage
{
	final static int EDITOR_WEIGHT = 8;
	private PropertyValueModel instantiationPolicyValueHolder;
	private PropertyValueModel policyTypeHolder;
	private PropertyValueModel factoryTypeHolder;

	InstantiationPolicyPropertiesPage(PropertyValueModel valueModel, WorkbenchContextHolder contextHolder)
	{
		super(valueModel, contextHolder);
	}
	
	private String getHelpTopicId()
	{
		return "descriptor.instantiation";
	}

	protected Component buildPage()
	{
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Use default constructor instantiation policy
		JRadioButton useDefaultConstructor = buildUseDefaultConstructorButton();

		// Use method instantiation policy
		JRadioButton useMethod = buildUseMethodButton();

		// Use method panel
		JPanel useMethodPanel = buildUseMethodPanel();
		buildUseMethodPanelEnabler(buildInstantiationPolicyTypeValueHolder(), useMethodPanel.getComponents());

		// Use factory instantiation policy
		JRadioButton useFactory = buildUseFactoryButton();

		// Use factory instantiation policy
		JPanel useFactoryPanel = buildUseFactoryPanel();
		buildFactoryPanelEnabler(buildInstantiationPolicyTypeValueHolder(), useFactoryPanel.getComponents());

		// Add all the widgets
		GroupBox groupBox = new GroupBox
		(
			new AbstractButton[] { useDefaultConstructor, useMethod, useFactory },
			new JComponent[]     { GroupBox.NO_PANE, useMethodPanel, useFactoryPanel }
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

		mainPanel.add(groupBox, constraints);
		addHelpTopicId(mainPanel, getHelpTopicId());
		
		return mainPanel;
	}

	protected JPanel buildUseFactoryPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel useFactoryPanel = new JPanel(new GridBagLayout());

		// Factory label
		JLabel factoryClassLabel = buildLabel("INSTANTIATION_POLICY_FACTORY_CLASS");

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		useFactoryPanel.add(factoryClassLabel, constraints);
		addAlignLeft(factoryClassLabel);

		// Factory chooser
		ClassChooserPanel factoryClassChooserPanel = ClassChooserTools.buildPanel(
			this.factoryTypeHolder,
			this.buildClassRepositoryHolder(),
			ClassChooserTools.buildDeclarableReferenceFilter(),
			factoryClassLabel,
			this.getWorkbenchContextHolder()
		);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		useFactoryPanel.add(factoryClassChooserPanel, constraints);
		factoryClassLabel.setLabelFor(factoryClassChooserPanel);
		addPaneForAlignment(factoryClassChooserPanel);
		
		// Factory method widgets
		JComponent factoryMethodWidgets = buildLabeledComponent(
			"INSTANTIATION_POLICY_FACTORY_METHOD",
			buildFactoryMethodListChooser()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		useFactoryPanel.add(factoryMethodWidgets, constraints);
		
		// Instantiation Method widgets
		JComponent instantiationMethodWidgets = buildLabeledComponent(
			"INSTANTIATION_POLICY_INSTANTIATION_METHOD",
			buildFactoryInstantiationMethodListChooser()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		useFactoryPanel.add(instantiationMethodWidgets, constraints);

		addHelpTopicId(useFactoryPanel, getHelpTopicId() + ".useFactory");
		return useFactoryPanel;
	}

	protected JPanel buildUseMethodPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel useMethodPanel = new JPanel(new GridBagLayout());

		// Method chooser
		ListChooser methodChooser = buildUseMethodListChooser();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		useMethodPanel.add(methodChooser, constraints);
		methodChooser.putClientProperty("labeledBy", new JLabel(" "));

		// Add a spacer
		Spacer spacer = new Spacer();

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		useMethodPanel.add(spacer, constraints);
		addAlignRight(spacer);

		addHelpTopicId(useMethodPanel, getHelpTopicId() + ".useMethod");
				
		return useMethodPanel;
	}
	
	private PropertyValueModel buildInstantiationPolicyHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.INSTANTIATION_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				MWDescriptorPolicy policy = ((MWMappingDescriptor) this.subject).getInstantiationPolicy();
				return policy.isActive() ? policy : null;
			}
		};
	}
	
	private PropertyValueModel buildInstantiationPolicyTypeValueHolder()
	{
		if (this.policyTypeHolder == null)
		{
			this.policyTypeHolder = new PropertyAspectAdapter(this.instantiationPolicyValueHolder,
					MWDescriptorInstantiationPolicy.POLICY_TYPE_PROPERTY) {
				protected Object getValueFromSubject()
				{
					return ((MWDescriptorInstantiationPolicy) this.subject).getPolicyType();
				}

				protected void setValueOnSubject(Object value)
				{
					((MWDescriptorInstantiationPolicy) this.subject).setPolicyType((String) value);
				}
			}; 
		}
		return this.policyTypeHolder; 
	}
	
	private JRadioButton buildUseDefaultConstructorButton()
	{
		return buildRadioButton(
				"INSTANTIATION_POLICY_USE_DEFAULT_CONSTRUCTOR",
				new RadioButtonModelAdapter(
						buildInstantiationPolicyTypeValueHolder(), MWDescriptorInstantiationPolicy.DEFAULT_CONSTRUCTOR));

	}
	
	private JRadioButton buildUseMethodButton()
	{
		return buildRadioButton(
				"INSTANTIATION_POLICY_USE_METHOD",
				new RadioButtonModelAdapter(
						buildInstantiationPolicyTypeValueHolder(), MWDescriptorInstantiationPolicy.METHOD));
	}

	
	private JRadioButton buildUseFactoryButton()
	{
		return buildRadioButton(
				"INSTANTIATION_POLICY_USE_FACTORY",
				new RadioButtonModelAdapter(
						buildInstantiationPolicyTypeValueHolder(), MWDescriptorInstantiationPolicy.FACTORY));
	}
	
	MWDescriptor descriptor() {
		return (MWDescriptor) this.selection();
	}

	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return InstantiationPolicyPropertiesPage.this.descriptor().getRepository();
			}
		};
	}
	
	private PropertyValueModel buildFactoryTypeHolder() 
	{
		return new PropertyAspectAdapter(this.instantiationPolicyValueHolder, MWDescriptorInstantiationPolicy.FACTORY_TYPE_PROPERTY) {
			protected Object getValueFromSubject() 
			{
				return ((MWDescriptorInstantiationPolicy) this.subject).getFactoryType();
			}
			protected void setValueOnSubject(Object value) 
			{
				MWClass type = (MWClass) value;
				((MWDescriptorInstantiationPolicy) this.subject).setFactoryType(type);
				//TODO possibly inform the user that we are adding this class to the repository
				//we refresh the first instance of this class that we find on the classpath.
				//in the 'Manage Non-Descriptor Classes' dialog they can choose which one to refresh
				if (type != null) {
					try {
						if (type.isStub()) {
							type.refresh();
						}	
					} catch (ExternalClassNotFoundException exception) {
						showClassLoadingException();
					}
				}				
			}
		};
	}
	
	private PropertyValueModel buildUseMethodSelectionHolder()
	{
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.instantiationPolicyValueHolder,
				MWDescriptorInstantiationPolicy.USE_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorInstantiationPolicy) this.subject).getUseMethod();
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorInstantiationPolicy) this.subject).setUseMethod((MWMethod)value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}
	
	private PropertyValueModel buildFactoryMethodSelectionHolder()
	{
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.instantiationPolicyValueHolder,
				MWDescriptorInstantiationPolicy.FACTORY_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorInstantiationPolicy) this.subject).getFactoryMethod();
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorInstantiationPolicy) this.subject).setFactoryMethod((MWMethod)value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}

	private PropertyValueModel buildInstantiationMethodSelectionHolder()
	{
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.instantiationPolicyValueHolder,
				MWDescriptorInstantiationPolicy.INSTANTIATION_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorInstantiationPolicy) this.subject).getInstantiationMethod();
			}

			protected void setValueOnSubject(Object value) {
				((MWDescriptorInstantiationPolicy) this.subject).setInstantiationMethod((MWMethod)value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}
				
	private PropertyValueModel buildUseMethodClassHolder() 
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWDescriptor.MW_CLASS_PROPERTY) 
		{
			protected Object getValueFromSubject() 
			{
				return ((MWDescriptor) this.subject).getMWClass();
			}
		};
	}
	
	private ListChooser buildUseMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
				buildUseMethodComboBoxModel(),
                DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}

	private CachingComboBoxModel buildUseMethodComboBoxModel()
	{
		return new IndirectComboBoxModel(buildUseMethodSelectionHolder(), buildUseMethodClassHolder()) {
			protected ListIterator listValueFromSubject(Object subject) {
				return InstantiationPolicyPropertiesPage.this.orderedFactoryInstantiationMethodChoices((MWClass) subject);
			}
		};
	}
	
	ListIterator orderedFactoryMethodChoices(MWClass mwClass) {
		List methodChoices = CollectionTools.sort(mwClass.candidateFactoryMethods());
		//need to add null into this list because it is a legitimate selection for this item.
		methodChoices.add(0, null);
		return methodChoices.listIterator();
	}

	ListIterator orderedFactoryInstantiationMethodChoices(MWClass mwClass) {		
		return CollectionTools.sort(mwClass.candidateFactoryInstantiationMethodsFor(descriptor().getMWClass())).listIterator();
	}

	private ListChooser buildFactoryMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
				buildFactoryMethodComboBoxModel());
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}

	private CachingComboBoxModel buildFactoryMethodComboBoxModel()
	{
		return new IndirectComboBoxModel(buildFactoryMethodSelectionHolder(), this.factoryTypeHolder) {
			protected ListIterator listValueFromSubject(Object subject) {
				return InstantiationPolicyPropertiesPage.this.orderedFactoryMethodChoices((MWClass) subject);
			}
		};
	}

	private CachingComboBoxModel buildInstantiationMethodComboBoxModel()
	{
		return new IndirectComboBoxModel(buildInstantiationMethodSelectionHolder(), this.factoryTypeHolder) {
			protected ListIterator listValueFromSubject(Object subject) {
				return InstantiationPolicyPropertiesPage.this.orderedFactoryInstantiationMethodChoices((MWClass) subject);
			}
		};
	}
		
	private ListChooser buildFactoryInstantiationMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
				buildInstantiationMethodComboBoxModel());
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
			
	protected void initialize(PropertyValueModel nodeHolder)
	{
		super.initialize(nodeHolder);
		this.instantiationPolicyValueHolder = buildInstantiationPolicyHolder();
		this.factoryTypeHolder = buildFactoryTypeHolder();
	}

	void showClassLoadingException() {
        LabelArea label = new LabelArea(resourceRepository().getString("PROBLEM_LOADING_CLASS")); 
		JOptionPane.showMessageDialog(this, label, resourceRepository().getString("PROBLEM_LOADING_CLASS_TITLE"), JOptionPane.ERROR_MESSAGE);
		this.factoryTypeHolder.setValue(null);	
	}
	
	private ComponentEnabler buildUseMethodPanelEnabler(
			PropertyValueModel useMethodHolder, Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(useMethodHolder)
		{
			protected Object transform(Object value)
			{
				
				return Boolean.valueOf(MWDescriptorInstantiationPolicy.METHOD.equals(value));
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
	private ComponentEnabler buildFactoryPanelEnabler(
			PropertyValueModel factoryHolder, Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(factoryHolder)
		{
			protected Object transform(Object value)
			{
				
				return Boolean.valueOf(MWDescriptorInstantiationPolicy.FACTORY.equals(value));
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}

}
