/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.EventsPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.InterfaceAliasPolicyPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisRootDescriptorTabbedPropertiesPage extends MappingDescriptorTabbedPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiXmlBundle.class,
		UiDescriptorBundle.class,
		UiQueryBundle.class,
		UiDescriptorXmlBundle.class,
	};


	public EisRootDescriptorTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		super.initializeTabs();
		addTab(new EisRootDescriptorInfoPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "DESCRIPTOR_INFO_TAB");
		addTab(buildClassInfoPropertiesPage(), "CLASS_INFO_TAB");
        addTab(new EisDescriptorQueryManagerPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "EIS_DESCRIPTOR_QUERIES_TAB");
        addTab(buildCachingPolicyPropertiesPage(), "EIS_ROOT_DESCRIPTOR_CACHING_TAB");
        addTab(new EisLockingPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "XML_DESCRIPTOR_LOCKING_TAB");
		
        addTab(buildInterfaceAliasPropertyValueModel(), InterfaceAliasPolicyPage.EDITOR_WEIGHT, 
				new InterfaceAliasPolicyPage(getNodeHolder(), getWorkbenchContextHolder()),  "INTERFACE_ALIAS_POLICY_MAPPING_DESCRIPTOR_TAB");
		addTab(buildEventsPolicyValueModel(), EventsPolicyPropertiesPage.EDITOR_WEIGHT, 
				buildEventsPolicyPropertiesPage(), "XML_DESCRIPTOR_EVENTS_TAB");
		addTab(buildReturningPropertyValueModel(), EisReturningPolicyPropertiesPage.EDITOR_WEIGHT, 
				new EisReturningPolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder()), "EIS_ROOT_DESCRIPTOR_RETURNING_TAB");
	}
	
	protected Component buildInheritancePolicyPropertiesPage() {
		return new EisInheritancePolicyPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected ComponentBuilder buildInheritancePolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private XmlInheritancePolicyPropertiesPage inheritancePolicyPage;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (inheritancePolicyPage == null)
					{
						inheritancePolicyPage = new EisInheritancePolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
					}
					return inheritancePolicyPage;
			}
		};
	}
	
	protected ComponentBuilder buildLockingPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private EisLockingPolicyPropertiesPage lockingPolicyPropertiesPage;
			
			public Component buildComponent(PropertyValueModel nodeHolder)
			{
				if (lockingPolicyPropertiesPage == null)
					{
						lockingPolicyPropertiesPage = new EisLockingPolicyPropertiesPage(nodeHolder, getWorkbenchContextHolder());
					}
					return lockingPolicyPropertiesPage;
			}
		};
	}
	
	protected PropertyValueModel buildReturningPropertyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
			new PropertyAspectAdapter( getSelectionHolder(), MWRootEisDescriptor.RETURNING_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				return ((MWRootEisDescriptor) subject).getReturningPolicy();
			}
		});
	}

	protected ComponentBuilder buildReturningPolicyPageBuilder()
	{
		return new ComponentBuilder() 
		{
			private EisReturningPolicyPropertiesPage returningPage;
			
			public Component buildComponent( PropertyValueModel nodeHolder)
			{
				if( returningPage == null)
				{
					returningPage = new EisReturningPolicyPropertiesPage( nodeHolder, getWorkbenchContextHolder());
				}
				return returningPage;
			}
		};
	}

	/**
	 * InterfaceAlias ValueModel for dynamic tab.
	 */
	protected PropertyValueModel buildInterfaceAliasPropertyValueModel()
	{
		return  buildDescriptorPolicyBooleanValueModel(
		new PropertyAspectAdapter(getSelectionHolder(), MWRootEisDescriptor.INTERFACE_ALIAS_POLICY_PROPERTY) 
		{
			public Object getValueFromSubject()
			{
				MWRootEisDescriptor desc = (MWRootEisDescriptor)subject;
				return desc.getInterfaceAliasPolicy();
			}
		});
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

}
