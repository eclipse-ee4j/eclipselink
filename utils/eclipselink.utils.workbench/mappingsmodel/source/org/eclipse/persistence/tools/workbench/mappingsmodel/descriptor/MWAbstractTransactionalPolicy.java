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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public abstract class MWAbstractTransactionalPolicy extends MWModel 
	implements MWTransactionalPolicy
{
	
	private MWRefreshCachePolicy refreshCachePolicy;
	
	private MWQueryManager queryManager;
	
	private MWCachingPolicy cachingPolicy;

    private volatile MWLockingPolicy lockingPolicy;

    /** defaults to false **/
	private volatile boolean readOnly;

	private volatile boolean conformResultsInUnitOfWork;

    private volatile String descriptorAlias;

	
	// ********** static methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAbstractTransactionalPolicy.class);

		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(MWRelationalTransactionalPolicy.class, "relational");
		ip.addClassIndicator(MWEisTransactionalPolicy.class, "eis");
		ip.addClassIndicator(MWOXTransactionalPolicy.class, "ox");

        ((XMLDirectMapping) descriptor.addDirectMapping("descriptorAlias", "descriptor-alias/text()")).setNullValue("");

        XMLDirectMapping readOnlyMapping = (XMLDirectMapping) descriptor.addDirectMapping("readOnly", "read-only/text()");
		readOnlyMapping.setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping refreshCachePolicyMapping = new XMLCompositeObjectMapping();
		refreshCachePolicyMapping.setAttributeName("refreshCachePolicy");
		refreshCachePolicyMapping.setReferenceClass(MWRefreshCachePolicy.class);
		refreshCachePolicyMapping.setXPath("refresh-cache-policy");
		descriptor.addMapping(refreshCachePolicyMapping);

		XMLCompositeObjectMapping cachingPolicyMapping = new XMLCompositeObjectMapping();
		cachingPolicyMapping.setAttributeName("cachingPolicy");
		cachingPolicyMapping.setReferenceClass(MWDescriptorCachingPolicy.class);
		cachingPolicyMapping.setXPath("caching-policy");
		descriptor.addMapping(cachingPolicyMapping);

		XMLCompositeObjectMapping queryManagerMapping = new XMLCompositeObjectMapping();
		queryManagerMapping.setAttributeName("queryManager");
		queryManagerMapping.setReferenceClass(MWQueryManager.class);
		queryManagerMapping.setXPath("query-manager");
		descriptor.addMapping(queryManagerMapping);
		
        XMLCompositeObjectMapping lockingPolicyMapping = new XMLCompositeObjectMapping();
        lockingPolicyMapping.setAttributeName("lockingPolicy");
        lockingPolicyMapping.setReferenceClass(MWDescriptorLockingPolicy.class);
        lockingPolicyMapping.setXPath("locking-policy");
        descriptor.addMapping(lockingPolicyMapping);

 		XMLDirectMapping criuowMapping = (XMLDirectMapping) descriptor.addDirectMapping("conformResultsInUnitOfWork", "conform-results-in-unit-of-work/text()");

		criuowMapping.setNullValue(Boolean.FALSE);
	
		return descriptor;
	}


	// ********** Constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	protected  MWAbstractTransactionalPolicy() {
		super();
	}

	protected MWAbstractTransactionalPolicy(MWTransactionalDescriptor parent) {
		super(parent);
	}


	// ********** Initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.queryManager = buildQueryManager();
		this.refreshCachePolicy = new MWRefreshCachePolicy(this);
		this.cachingPolicy 	= new MWDescriptorCachingPolicy(this);
        this.lockingPolicy  = buildLockingPolicy();
        this.descriptorAlias = getDescriptor().shortName();
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.refreshCachePolicy);
		children.add(this.queryManager);
		children.add(this.cachingPolicy);
        children.add(this.lockingPolicy);
	}
	
	protected abstract MWQueryManager buildQueryManager();
	
    protected abstract MWLockingPolicy buildLockingPolicy();
	 
    private MWTransactionalDescriptor getDescriptor() {
        return (MWTransactionalDescriptor) getParent();
    }
    
    
	// **************** accessors *****************

	public MWRefreshCachePolicy getRefreshCachePolicy() {
		return this.refreshCachePolicy;
	}
	
	public boolean isConformResultsInUnitOfWork() {
		// this method name not good, but it's convention to have it... for boolean attributes
		return this.conformResultsInUnitOfWork;
	}
	
	public void setConformResultsInUnitOfWork(boolean newValue) {
		boolean oldValue = this.conformResultsInUnitOfWork;
		this.conformResultsInUnitOfWork = newValue;
		this.firePropertyChanged(CONFORM_RESULTS_IN_UNIT_OF_WORK_PROPERTY, oldValue, newValue);
	}
		
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public void setReadOnly(boolean newValue) {
		boolean oldValue = this.readOnly;
		this.readOnly = newValue;
		this.firePropertyChanged(READ_ONLY_PROPERTY, oldValue, newValue);
	}
	
	public MWCachingPolicy getCachingPolicy() {
		return this.cachingPolicy;
	}
	
    public MWLockingPolicy getLockingPolicy() {
        return this.lockingPolicy;
    }
	
	public MWQueryManager getQueryManager() {
		return this.queryManager;
	}
	
    // ************* Descriptor Alias **************
    
    public String getDescriptorAlias() {
        return this.descriptorAlias;
    }
    
    public void setDescriptorAlias(String descriptorAlias) {
        String oldDescriptorAlias = getDescriptorAlias();
        this.descriptorAlias = descriptorAlias;
        firePropertyChanged(DESCRIPTOR_ALIAS_PROPERTY, oldDescriptorAlias, descriptorAlias);
    }



	/** Used to keep up to date with inheritance changes
	 * @see MWTransactionalPolicy.descriptorInheritanceChanged() */
	public void descriptorInheritanceChanged() {
		this.cachingPolicy.descriptorInheritanceChanged();
	}

	
	//*************** runtime conversion *************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		this.refreshCachePolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		
		runtimeDescriptor.setShouldAlwaysConformResultsInUnitOfWork(isConformResultsInUnitOfWork());
		runtimeDescriptor.setShouldBeReadOnly(isReadOnly());
        runtimeDescriptor.setAlias(getDescriptorAlias());
		
		this.queryManager.adjustRuntimeDescriptor(runtimeDescriptor);
		this.cachingPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
        this.lockingPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
	}
	

	// ************ TopLink Methods ***********
	
	public MWAbstractTransactionalPolicy getValueForTopLink() {
		return this;
	}

}
