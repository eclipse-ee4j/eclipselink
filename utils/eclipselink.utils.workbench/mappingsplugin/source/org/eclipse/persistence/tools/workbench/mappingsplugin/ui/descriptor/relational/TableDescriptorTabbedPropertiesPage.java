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

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.EventsPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.InterfaceAliasPolicyPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



final class TableDescriptorTabbedPropertiesPage extends MappingDescriptorTabbedPropertiesPage 
{
	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiDescriptorBundle.class,
		UiQueryBundle.class,
		UiDescriptorRelationalBundle.class,
	};


    private TableDescriptorQueryManagerPropertiesPage queryManagerPage;
    
	TableDescriptorTabbedPropertiesPage(WorkbenchContext context) 
	{
		super(context);
	}

	protected void initializeTabs() 
	{
		super.initializeTabs();
        this.queryManagerPage = new TableDescriptorQueryManagerPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
        
		addTab(new TableDescriptorInfoPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "DESCRIPTOR_INFO_TAB");
		addTab(buildClassInfoPropertiesPage(), "CLASS_INFO_TAB");
		addTab(this.queryManagerPage, "RELATIONAL_DESCRIPTOR_QUERIES_TAB");
        addTab(buildCachingPolicyPropertiesPage(), "RELATIONAL_DESCRIPTOR_CACHING_TAB");
        addTab(new TableLockingPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "RELATIONAL_DESCRIPTOR_LOCKING_TAB");
		
        addTab(buildInterfaceAliasPropertyValueModel(), InterfaceAliasPolicyPage.EDITOR_WEIGHT, 
				new InterfaceAliasPolicyPage(getNodeHolder(), getWorkbenchContextHolder()),  "INTERFACE_ALIAS_POLICY_MAPPING_DESCRIPTOR_TAB");
		addTab(buildEventsPolicyValueModel(), EventsPolicyPropertiesPage.EDITOR_WEIGHT, 
				buildEventsPolicyPropertiesPage(), "RELATIONAL_DESCRIPTOR_EVENTS_TAB");
		addTab(buildMultiTablePropertyValueModel(), MultiTableInfoPropertiesPage.EDITOR_WEIGHT, 
				new MultiTableInfoPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "RELATIONAL_DESCRIPTOR_MULTI_TABLE_TAB");
		addTab(buildReturningPropertyValueModel(), RelationalReturningPolicyPropertiesPage.EDITOR_WEIGHT, 
				new RelationalReturningPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "RELATIONAL_DESCRIPTOR_RETURNING_TAB");
	}
	
	protected Component buildInheritancePolicyPropertiesPage() {
		return new RelationalInheritancePolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	/**
	 * InheritancePolicy <code>ComponentBuilder</code> for dynamic tab.
	 */
	protected ComponentBuilder buildInheritancePolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private RelationalInheritancePolicyPropertiesPage inheritancePolicyPage;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (inheritancePolicyPage == null)
					{
						inheritancePolicyPage = new RelationalInheritancePolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
					}
					return inheritancePolicyPage;
			}
		};
	}
	
	/**
	 * InterfaceAlias <code>ComponentBuilder</code> for dynamic tab.
	 */
	protected ComponentBuilder buildInterfaceAliasPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private InterfaceAliasPolicyPage interfaceAliasPolicyPage;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (interfaceAliasPolicyPage == null)
					{
						interfaceAliasPolicyPage = new InterfaceAliasPolicyPage(nodeHolder, getWorkbenchContextHolder());
					}
					return interfaceAliasPolicyPage;
			}
		};
	}
	
	/**
	 * Relational Locking Policy <code>ComponentBuilder</code> for dynamic tab.
	 */
	protected ComponentBuilder buildLockingPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private TableLockingPolicyPropertiesPage lockingPolicyPage;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (lockingPolicyPage == null)
				{
					lockingPolicyPage = new TableLockingPolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
				}
					return lockingPolicyPage;
			}
		};
	}
	
	protected ComponentBuilder buildMultiTableInfoPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private MultiTableInfoPropertiesPage multiTablePage;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (multiTablePage == null)
				{
					multiTablePage = new MultiTableInfoPropertiesPage(nodeHolder, getWorkbenchContextHolder());
				}
					return multiTablePage;
			}
		};
	}
	
	/**
	 * InterfaceAlias ValueModel for dynamic tab.
	 */
	protected PropertyValueModel buildInterfaceAliasPropertyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
		new PropertyAspectAdapter(getSelectionHolder(), MWTableDescriptor.INTERFACE_ALIAS_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWTableDescriptor desc = (MWTableDescriptor)subject;
				return desc.getInterfaceAliasPolicy();
			}
		});
	}
	
	protected PropertyValueModel buildMultiTablePropertyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
		new PropertyAspectAdapter(getSelectionHolder(), MWTableDescriptor.MULTI_TABLE_INFO_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWTableDescriptor desc = (MWTableDescriptor)subject;
				return desc.getMultiTableInfoPolicy();
			}
		});
	}
	
	protected PropertyValueModel buildReturningPropertyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
			new PropertyAspectAdapter( getSelectionHolder(), MWTableDescriptor.RETURNING_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWTableDescriptor desc = ( MWTableDescriptor)subject;
				return desc.getReturningPolicy();
			}
		});
	}

	protected ComponentBuilder buildReturningPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private RelationalReturningPolicyPropertiesPage returningPage;
			
			public Component buildComponent( PropertyValueModel nodeHolder)
			{
				if( returningPage == null)
				{
					returningPage = new RelationalReturningPolicyPropertiesPage( nodeHolder, getWorkbenchContextHolder());
				}
				return returningPage;
			}
		};
	}
	    
    public void selectQueryKey(MWQueryKey queryKey) {
        setSelectedTab(this.queryManagerPage);
        this.queryManagerPage.selectQueryKey(queryKey);
    }
}
