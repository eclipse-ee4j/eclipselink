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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;



/**
 * This class describes the default settings for a <code>MWProject</code>. These settings include default caching
 * policy, use of method accessing, and the Advanced descriptor properties that should be added to a newly created
 * descriptor.  
 * 
 * @version 10.1.3
 */
public abstract class MWProjectDefaultsPolicy extends MWModel
{
	public static final String COPY_POLICY = "Copy Policy";
	public static final String AFTER_LOAD_POLICY = "After Load";
	public static final String INSTANTIATION_POLICY = "Instantiation";
	public static final String INHERITANCE_POLICY = "Inheritance";

	private Collection defaultPolicies;
		public static final String DEFAULT_POLICY_COLLECTION = "defaultPolicyCollection";
	
	private Map policyBuilderMap;
	
	private MWCachingPolicy cachingPolicy;
	
	private volatile boolean methodAccessing;
		public final static String METHOD_ACCESSING_PROPERTY = "methodAccessing";	
	
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWProjectDefaultsPolicy.class);
				
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalProjectDefaultsPolicy.class, "relational");
		ip.addClassIndicator(MWEisProjectDefaultsPolicy.class, "eis");
		ip.addClassIndicator(MWOXProjectDefaultsPolicy.class, "o-x");
		
		descriptor.addDirectMapping("methodAccessing", "use-method-accessing/text()");
		
		XMLCompositeDirectCollectionMapping defaultPoliciesMapping = new XMLCompositeDirectCollectionMapping();
		defaultPoliciesMapping.setAttributeName("defaultPolicies");
		defaultPoliciesMapping.setXPath("default-descriptor-policies/policy-type/text()");
		ObjectTypeConverter converter = new ObjectTypeConverter();
		converter.addConversionValue(COPY_POLICY, COPY_POLICY);
		converter.addConversionValue(AFTER_LOAD_POLICY, AFTER_LOAD_POLICY);
		converter.addConversionValue(INSTANTIATION_POLICY, INSTANTIATION_POLICY);
		converter.addConversionValue(INHERITANCE_POLICY, INHERITANCE_POLICY);
        converter.addConversionValue(MWTransactionalProjectDefaultsPolicy.EVENTS_POLICY, MWTransactionalProjectDefaultsPolicy.EVENTS_POLICY);
        converter.addConversionValue(MWTransactionalProjectDefaultsPolicy.RETURNING_POLICY, MWTransactionalProjectDefaultsPolicy.RETURNING_POLICY);
		converter.addConversionValue(MWRelationalProjectDefaultsPolicy.INTERFACE_ALIAS_POLICY, MWRelationalProjectDefaultsPolicy.INTERFACE_ALIAS_POLICY);
		converter.addConversionValue(MWRelationalProjectDefaultsPolicy.MULTI_TABLE_INFO_POLICY, MWRelationalProjectDefaultsPolicy.MULTI_TABLE_INFO_POLICY);
		defaultPoliciesMapping.setValueConverter(converter);
		descriptor.addMapping(defaultPoliciesMapping);
		
		XMLCompositeObjectMapping projectCachingPolicyMapping = new XMLCompositeObjectMapping();
		projectCachingPolicyMapping.setAttributeName("cachingPolicy");
		projectCachingPolicyMapping.setSetMethodName("setProjectCachingPolicyForTopLink");
		projectCachingPolicyMapping.setGetMethodName("getProjectCachingPolicyForTopLink");
		projectCachingPolicyMapping.setReferenceClass(MWTransactionalProjectCachingPolicy.class);
		projectCachingPolicyMapping.setXPath("caching-policy");
		descriptor.addMapping(projectCachingPolicyMapping);
		
		return descriptor;
	}
	
	protected MWProjectDefaultsPolicy() {
		super();
	}

	/**
	 * @param parent
	 */
	public MWProjectDefaultsPolicy(MWModel parent)
	{
		super(parent);
	}
	
	protected void initialize()
	{
		super.initialize();
		this.policyBuilderMap = new HashMap(); 
		initializePolicyDescriptors();
	}

	protected void initialize(Node parent)
	{
		super.initialize(parent);
		this.defaultPolicies = new Vector();
		this.cachingPolicy = buildCachingPolicy();
	}

	protected void addChildrenTo(List children)
	{
		super.addChildrenTo(children);
		children.add(this.cachingPolicy);
	}
	
	protected MWCachingPolicy buildCachingPolicy()
	{
		return new MWNullCachingPolicy(this);
	}
	
	protected void initializePolicyDescriptors()
	{
		addPolicyDescriptor(COPY_POLICY, new CopyPolicyDescriptor());
		addPolicyDescriptor(AFTER_LOAD_POLICY, new AfterLoadPolicyDescriptor());
		addPolicyDescriptor(INHERITANCE_POLICY, new InheritancePolicyDescriptor());
		addPolicyDescriptor(INSTANTIATION_POLICY, new InstantiationPolicyDescriptor());
	}
	
	public Iterator defaultPolicies()
	{
		return this.defaultPolicies.iterator();
	}
	
	protected void addPolicyDescriptor(String policyKey, PolicyDescriptor descriptor)
	{
		this.policyBuilderMap.put(policyKey, descriptor);
	}
	
	protected PolicyDescriptor getPolicyDescriptor(String policyKey)
	{
		return (PolicyDescriptor) this.policyBuilderMap.get(policyKey);
	}
	
	public void applyAdvancedPolicyDefaults(MWMappingDescriptor descriptor)
	{
		for (Iterator defaults = this.defaultPolicies.iterator(); defaults.hasNext();)
		{
			getPolicyDescriptor((String)defaults.next()).applyPolicyToDescriptor(descriptor);
		}
	}
	
	public boolean containsAdvancePolicyDefault(String policyName)
	{
		return this.defaultPolicies.contains(policyName);
	}
	
	public void addAdvancedPolicyDefault(String policyName)
	{
		this.defaultPolicies.add(policyName);
		fireItemAdded(DEFAULT_POLICY_COLLECTION, policyName);
	}
	
	public void removeAdvancedPolicyDefault(String policyName)
	{
		this.defaultPolicies.remove(policyName);
		fireItemRemoved(DEFAULT_POLICY_COLLECTION, policyName);
	}
	
	// TODO convert all the implementations of this interface to singletons
	protected interface PolicyDescriptor
	{
		void applyPolicyToDescriptor(MWMappingDescriptor descriptor);

	}
	
	private class AfterLoadPolicyDescriptor implements PolicyDescriptor
	{
		public void applyPolicyToDescriptor(MWMappingDescriptor descriptor) {
			if (!descriptor.getAfterLoadingPolicy().isActive()) {
				descriptor.addAfterLoadingPolicy();
			}
		}
	}
	
	private class CopyPolicyDescriptor implements PolicyDescriptor
	{
		public void applyPolicyToDescriptor(MWMappingDescriptor descriptor) {
			if (!descriptor.getCopyPolicy().isActive()) {
				descriptor.addCopyPolicy();
			}
		}	
	}
	
	private class InstantiationPolicyDescriptor implements PolicyDescriptor
	{
		public void applyPolicyToDescriptor(MWMappingDescriptor descriptor) {
			if (!descriptor.getInstantiationPolicy().isActive()) {
				descriptor.addInstantiationPolicy();
			}
		}
	}
	
	private class InheritancePolicyDescriptor implements PolicyDescriptor
	{
		public void applyPolicyToDescriptor(MWMappingDescriptor descriptor) {
			if (!descriptor.getInheritancePolicy().isActive()) {
				descriptor.addInheritancePolicy();
			}
		}	
	}
	
	
	public MWCachingPolicy getCachingPolicy()
	{
		return this.cachingPolicy;
	}
	
	public boolean isMethodAccessing() {
		return this.methodAccessing;
	}

	public void setMethodAccessing(boolean newMethodAccessing) {
		boolean old = this.methodAccessing;
		this.methodAccessing = newMethodAccessing;
		firePropertyChanged(METHOD_ACCESSING_PROPERTY, old, this.methodAccessing);
	}
	
	
	// *************** TopLink only methods ***************
	
	private MWTransactionalProjectCachingPolicy getProjectCachingPolicyForTopLink()
	{
		return (MWTransactionalProjectCachingPolicy) this.cachingPolicy.getPersistedPolicy();
	}
	
	private void setProjectCachingPolicyForTopLink(MWTransactionalProjectCachingPolicy cachingPolicy)
	{
		if (cachingPolicy == null)
		{
			this.cachingPolicy = buildCachingPolicy();
		}
		else
		{
			this.cachingPolicy = cachingPolicy;
		}
	}

	protected void adjustRuntimeProject(Project project)
	{
	}

}
