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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;


/**
 * @author jobracke
 */
public abstract class MappingDescriptorTabbedPropertiesPage extends TabbedPropertiesPage
{
	
	private PropertyValueModel transactionalPolicyHolder;

    private CachingPolicyPropertiesPage cachingPolicyPage;
    private DescriptorClassInfoTabbedPropertiesPage classInfoPropertiesPage;
    
	protected MappingDescriptorTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.transactionalPolicyHolder = buildTransactionalPolicyHolder();
	}

	protected PropertyValueModel buildTransactionalPolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			public Object getValueFromSubject() {
				return ((MWMappingDescriptor) this.subject) .getTransactionalPolicy();
			}
		};
	}
	
	protected void initializeTabs()
	{		
		addTab(buildCopyPolicyValueModel(), CopyPolicyPropertiesPage.EDITOR_WEIGHT,
				new CopyPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "COPY_POLICY_MAPPING_DESCRIPTOR_TAB");
		addTab(buildAfterLoadingPolicyValueModel(), AfterLoadingPropertiesPage.EDITOR_WEIGHT,
				new AfterLoadingPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "AFTER_LOADING_POLICY_MAPPING_DESCRIPTOR_TAB");
		addTab(buildInstantiationPolicyValueModel(), InstantiationPolicyPropertiesPage.EDITOR_WEIGHT,
				new InstantiationPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "INSTANTIATION_POLICY_MAPPING_DESCRIPTOR_TAB");
		addTab(buildInheritancePolicyValueModel(), InheritancePolicyPropertiesPage.EDITOR_WEIGHT,
				buildInheritancePolicyPropertiesPage(), "INHERITANCE_POLICY_MAPPING_DESCRIPTOR_TAB");
	}
	
    protected DescriptorClassInfoTabbedPropertiesPage buildClassInfoPropertiesPage() {
        this.classInfoPropertiesPage = new DescriptorClassInfoTabbedPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
        return this.classInfoPropertiesPage;
    }
    
	/**
	 * MWCopyPolicy ValueModel for dynamic tab.
	 */
	private PropertyValueModel buildCopyPolicyValueModel()
	{
		return buildDescriptorPolicyBooleanValueModel( 
		new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.COPY_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWMappingDescriptor desc = (MWMappingDescriptor)subject;
				return  desc.getCopyPolicy();
			}
		});
	}
	
	/**
	 * MWCopyPolicy ValueModel for dynamic tab.
	 */
	private PropertyValueModel buildAfterLoadingPolicyValueModel()
	{
		return buildDescriptorPolicyBooleanValueModel( 
		new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.AFTER_LOADING_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWMappingDescriptor desc = (MWMappingDescriptor)subject;
				return desc.getAfterLoadingPolicy();
			}
		});
	}
	
	/**
	 * MWInstantiationPolicy ValueModel for dynamic tab.
	 */
	private PropertyValueModel buildInstantiationPolicyValueModel()
	{
		return buildDescriptorPolicyBooleanValueModel(
		new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.INSTANTIATION_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWMappingDescriptor desc = (MWMappingDescriptor)subject;
				return desc.getInstantiationPolicy();
			}
		});
	}	
	
	/**
	 * InheritancePolicy ValueModel for dynamic tab.  This is placed
	 * in the abstract MappingDescriptor implementation because the same
	 * model can be shared across OX, EIS, and Relational descriptor types.
	 */
	protected PropertyValueModel buildInheritancePolicyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
		new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.INHERITANCE_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{	
				MWMappingDescriptor desc = (MWMappingDescriptor)subject;
				return desc.getInheritancePolicy();
			}
		});
	}
	
	protected PropertyValueModel buildEventsPolicyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
		new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.EVENTS_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWMappingDescriptor desc = (MWMappingDescriptor)subject;
				return desc.getEventsPolicy();
			}
		});
	}

	protected PropertyValueModel buildDescriptorPolicyBooleanValueModel(PropertyValueModel valueModel)
	{
		return new TransformationPropertyValueModel(valueModel) {
			protected Object transform(Object value)
			{
				// if the given policy is not active or the policy is null
				// because the node has been "unset", then don't show the editor
					return Boolean.valueOf(value != null && ((MWDescriptorPolicy)value).isActive());
			}
		};
	}
	
	private ComponentBuilder buildCopyPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private CopyPolicyPropertiesPage copyPolicyPage;
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (copyPolicyPage == null)
				{
					copyPolicyPage = new CopyPolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
				}
				return copyPolicyPage;
			}
		};
	}
	
	private ComponentBuilder buildInstantiationPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private InstantiationPolicyPropertiesPage instantiationPolicyPage;
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (instantiationPolicyPage == null)
				{
					instantiationPolicyPage = new InstantiationPolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
				}
				return instantiationPolicyPage;
			}
		};
	}
	
	private ComponentBuilder buildAfterLoadingPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private AfterLoadingPropertiesPage afterLoadingPolicyPage;
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (afterLoadingPolicyPage == null)
				{
					afterLoadingPolicyPage = new AfterLoadingPropertiesPage(nodeHolder, getWorkbenchContextHolder());
				}
				return afterLoadingPolicyPage;
			}
		};
	}
	
	protected abstract Component buildInheritancePolicyPropertiesPage();

	protected abstract ComponentBuilder buildInheritancePolicyPageBuilder();
	
	protected PropertyValueModel buildCachingPolicyValueModel() {

		PropertyAspectAdapter cachingPolicyHolder = new PropertyAspectAdapter(this.transactionalPolicyHolder)
		{
			public Object getValueFromSubject()
			{
				return ((MWTransactionalPolicy) this.subject).getCachingPolicy();
			}
		};

		return new TransformationPropertyValueModel(cachingPolicyHolder)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return Boolean.FALSE;

				return Boolean.valueOf(!(value instanceof MWNullCachingPolicy));
			}
		};	
	}

	protected CachingPolicyPropertiesPage buildCachingPolicyPropertiesPage() {
		this.cachingPolicyPage = new CachingPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());      
        return this.cachingPolicyPage;
	}

	protected EventsPolicyPropertiesPage buildEventsPolicyPropertiesPage() {
		return new EventsPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected ComponentBuilder buildEventsPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private EventsPolicyPropertiesPage eventsPolicyPropertiesPage;
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (eventsPolicyPropertiesPage == null)
				{
					eventsPolicyPropertiesPage = new EventsPolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
				}
				return eventsPolicyPropertiesPage;
			}
		};
	}
    
    // This should only be called for Root EIS and Table descriptors, probably needs to be refactored
    public void selectCachingPolicyPage() {
        setSelectedTab(this.cachingPolicyPage);      
    }
    
    public void selectMethod(MWMethod method) {
        setSelectedTab(this.classInfoPropertiesPage);
        this.classInfoPropertiesPage.selectMethod(method);
    }
    
}
