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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.NoIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftIdentityMap;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DoesExistQuery;

/**
 * Describes the default project caching policy for a Transactional (EIS and Relational) based projects.
 * These settings will be applied to all descriptors who do not have their own Caching Policy specified.
 * 
 * @author jobracke
 */
public final class MWTransactionalProjectCachingPolicy extends MWModel implements MWCachingPolicy
{
	private volatile CacheCoordinationOption cacheCoordination;
    private static TopLinkOptionSet cacheCoordinationOptions;
    
	private volatile CacheIsolationOption cacheIsolation;
    private static TopLinkOptionSet cacheIsolationOptions;

    private volatile int cacheSize;

	private volatile CacheTypeOption cacheType;
	private static TopLinkOptionSet cacheTypeOptions;
    
    private volatile ExistenceCheckingOption existenceChecking;
    private static TopLinkOptionSet existenceCheckingOptions;

	private volatile MWCacheExpiry cacheExpiry;
	
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWTransactionalProjectCachingPolicy.class);

		// Cache Size
		XMLDirectMapping cacheSizeMapping = (XMLDirectMapping) descriptor.addDirectMapping("cacheSize", "caching-size/text()");
		cacheSizeMapping.setNullValue(new Integer(DEFAULT_CACHE_SIZE));

		// Cache Type
		XMLDirectMapping cacheTypeMapping = (XMLDirectMapping) descriptor.addDirectMapping("cacheType", "cache-type/text()");
		ObjectTypeConverter cacheTypeConverter = new ObjectTypeConverter();
		cacheTypeOptions().addConversionValuesForTopLinkTo(cacheTypeConverter);
		cacheTypeMapping.setConverter(cacheTypeConverter);
		cacheTypeMapping.setNullValue(cacheTypeOptions().topLinkOptionForMWModelOption(DEFAULT_CACHE_TYPE));

		// Existence Checking
		ObjectTypeConverter existenceCheckingConverter = new ObjectTypeConverter();
		existenceCheckingOptions().addConversionValuesForTopLinkTo(existenceCheckingConverter);
		XMLDirectMapping existenceCheckingMapping = new XMLDirectMapping();
		existenceCheckingMapping.setAttributeName("existenceChecking");
		existenceCheckingMapping.setNullValue(existenceCheckingOptions().topLinkOptionForMWModelOption(DEFAULT_EXISTENCE_CHECKING));
		existenceCheckingMapping.setXPath("existence-checking/text()");
		existenceCheckingMapping.setConverter(existenceCheckingConverter);
		descriptor.addMapping(existenceCheckingMapping);		

		// Cache Coordination
		ObjectTypeConverter cacheCoordinationConverter = new ObjectTypeConverter();
        cacheCoordinationOptions().addConversionValuesForTopLinkTo(cacheCoordinationConverter);
		XMLDirectMapping cacheCoordinationMapping = new XMLDirectMapping();
		cacheCoordinationMapping.setAttributeName("cacheCoordination");
		cacheCoordinationMapping.setXPath("cache-coordination/text()");
		cacheCoordinationMapping.setNullValue(cacheCoordinationOptions().topLinkOptionForMWModelOption(DEFAULT_CACHE_COORDINATION));
		cacheCoordinationMapping.setConverter(cacheCoordinationConverter);
		descriptor.addMapping(cacheCoordinationMapping);		

		// Cache Isolation
		ObjectTypeConverter cacheIsolationConverter = new ObjectTypeConverter();
        cacheIsolationOptions().addConversionValuesForTopLinkTo(cacheIsolationConverter);
		XMLDirectMapping cacheIsolationMapping = new XMLDirectMapping();
		cacheIsolationMapping.setAttributeName("cacheIsolation");
		cacheIsolationMapping.setXPath("cache-isolation/text()");
		cacheIsolationMapping.setConverter(cacheIsolationConverter);
		cacheIsolationMapping.setNullValue(cacheIsolationOptions().topLinkOptionForMWModelOption(DEFAULT_CACHE_ISOLATION));
		descriptor.addMapping(cacheIsolationMapping);		

        XMLCompositeObjectMapping cacheExpiryMapping = new XMLCompositeObjectMapping();
        cacheExpiryMapping.setAttributeName("cacheExpiry");
        cacheExpiryMapping.setReferenceClass(MWDescriptorCacheExpiry.class);
        cacheExpiryMapping.setXPath("cache-expiry");
        descriptor.addMapping(cacheExpiryMapping);
		
		return descriptor;
	}

    public synchronized static TopLinkOptionSet cacheCoordinationOptions() {
        if (cacheCoordinationOptions == null) {
            List list = new ArrayList();
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_NONE, "CACHING_POLICY_CACHE_COORDINATION_NONE", ClassDescriptor.DO_NOT_SEND_CHANGES));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_SYNCHRONIZE_CHANGES, "CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES", ClassDescriptor.SEND_OBJECT_CHANGES));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS, "CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS", ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS, "CACHING_POLICY_CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS", ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
            cacheCoordinationOptions = new TopLinkOptionSet(list);
        }
        
        return cacheCoordinationOptions;
    }
    public synchronized static TopLinkOptionSet cacheIsolationOptions() {
        if (cacheIsolationOptions == null) {
            List list = new ArrayList();
            list.add(new CacheIsolationOption(CACHE_ISOLATION_ISOLATED, "CACHING_POLICY_CACHE_ISOLATION_ISOLATED"));
            list.add(new CacheIsolationOption(CACHE_ISOLATION_SHARED, "CACHING_POLICY_CACHE_ISOLATION_SHARED"));
            cacheIsolationOptions = new TopLinkOptionSet(list);
        }
        
        return cacheIsolationOptions;
    }
	

	public synchronized static TopLinkOptionSet cacheTypeOptions() {
		if (cacheTypeOptions == null) {
            List list = new ArrayList();
            list.add(new CacheTypeOption(CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE, "ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_SOFTCACHEWEAKIDENTITYMAP", SoftCacheWeakIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_WEAK_WITH_HARD_SUBCACHE, "ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_HARDCACHEWEAKIDENTITYMAP", HardCacheWeakIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_WEAK, "ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_WEAKIDENTITYMAP", WeakIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_SOFT, "ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_SOFTIDENTITYMAP", SoftIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_FULL, "ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_FULLIDENTITYMAP", FullIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_NONE, "ORACLE_TOPLINK_INTERNAL_IDENTITYMAPS_NOIDENTITYMAP", NoIdentityMap.class.getName()));
		    cacheTypeOptions = new TopLinkOptionSet(list);
		}
		
		return cacheTypeOptions;
	}	
    
    public synchronized static TopLinkOptionSet existenceCheckingOptions() {
        if (existenceCheckingOptions == null) {
            List list = new ArrayList();
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_CHECK_CACHE, "CACHING_POLICY_EXISTENCE_CHECKING_CHECK_CACHE", DoesExistQuery.CheckCache));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_CHECK_DATABASE, "CACHING_POLICY_EXISTENCE_CHECKING_CHECK_DATABASE", DoesExistQuery.CheckDatabase));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_ASSUME_EXISTENCE, "CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_EXISTENCE", DoesExistQuery.AssumeExistence));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE, "CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE", DoesExistQuery.AssumeNonExistence));
            existenceCheckingOptions = new TopLinkOptionSet(list);
        }
       
        return existenceCheckingOptions;
    }   
		
	private MWTransactionalProjectCachingPolicy() {
		super();
	}

	MWTransactionalProjectCachingPolicy(MWTransactionalProjectDefaultsPolicy parent) {
		super(parent);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.cacheCoordination      = (CacheCoordinationOption) cacheCoordinationOptions().topLinkOptionForMWModelOption(DEFAULT_CACHE_COORDINATION);
        this.cacheIsolation         = (CacheIsolationOption) cacheIsolationOptions().topLinkOptionForMWModelOption(DEFAULT_CACHE_ISOLATION);
		this.cacheSize              = DEFAULT_CACHE_SIZE;
		this.cacheType              = (CacheTypeOption) cacheTypeOptions().topLinkOptionForMWModelOption(DEFAULT_CACHE_TYPE);
		this.cacheExpiry            = new MWDescriptorCacheExpiry(this);
		this.existenceChecking      = (ExistenceCheckingOption) existenceCheckingOptions().topLinkOptionForMWModelOption(DEFAULT_EXISTENCE_CHECKING);
	}
	
    protected void addChildrenTo(List list) {
        super.addChildrenTo(list);
        list.add(this.cacheExpiry);
    }
    
	public void initializeFrom(MWCachingPolicy otherPolicy) {
		throw new UnsupportedOperationException();
	}
	public CacheCoordinationOption getCacheCoordination()
	{
		return this.cacheCoordination;
	}

	public CacheIsolationOption getCacheIsolation()
	{
		return this.cacheIsolation;
	}

	public int getCacheSize()
	{
		return this.cacheSize;
	}

	public CacheTypeOption getCacheType()
	{
		return this.cacheType;
	}

	public ExistenceCheckingOption getExistenceChecking()
	{
		return this.existenceChecking;
	}

	public void setCacheCoordination(CacheCoordinationOption cacheCoordination)
	{
        CacheCoordinationOption oldCacheCoordination = this.cacheCoordination;
		this.cacheCoordination = cacheCoordination;
		firePropertyChanged(CACHE_COORDINATION_PROPERTY, oldCacheCoordination, cacheCoordination);
	}

	public void setCacheIsolation(CacheIsolationOption cacheIsolation)
	{
        CacheIsolationOption oldCacheIsolation = this.cacheIsolation;
		this.cacheIsolation = cacheIsolation;
		firePropertyChanged(CACHE_ISOLATION_PROPERTY, oldCacheIsolation, cacheIsolation);

		if (cacheIsolation.getMWModelOption() == CACHE_ISOLATION_ISOLATED) {
			setCacheCoordination((CacheCoordinationOption) cacheCoordinationOptions().topLinkOptionForMWModelOption(CACHE_COORDINATION_NONE));
		}
	}

	public void setCacheSize(int cacheSize)
	{
		int oldCacheSize = this.cacheSize;
		this.cacheSize = cacheSize;
		firePropertyChanged(CACHE_SIZE_PROPERTY, oldCacheSize, cacheSize);
	}

	public void setCacheType(CacheTypeOption cacheType)
	{
		CacheTypeOption oldCacheType = this.cacheType;
		this.cacheType = cacheType;
		firePropertyChanged(CACHE_TYPE_PROPERTY, oldCacheType, cacheType);
	}

	public void setCacheType(String cacheTypeString) {
	    setCacheType((CacheTypeOption) cacheTypeOptions().topLinkOptionForMWModelOption(cacheTypeString));
	}

	public void setExistenceChecking(ExistenceCheckingOption newExistenceChecking)
	{
		Object oldValue = this.existenceChecking;
		this.existenceChecking = newExistenceChecking;
		firePropertyChanged(EXISTENCE_CHECKING_PROPERTY, oldValue, this.existenceChecking);
	}
    
    public void setExistenceChecking(String existenceChecking) {
        setExistenceChecking((ExistenceCheckingOption) existenceCheckingOptions().topLinkOptionForMWModelOption(existenceChecking));
    }


    public MWCacheExpiry getCacheExpiry() {
        return this.cacheExpiry;
    }
    
    public void setUseProjectDefaultCacheExpiry(boolean useProjectCacheExpiry) {
        throw new UnsupportedOperationException("Does not apply for Project caching policy");
    }    
    
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		throw new UnsupportedOperationException();
	}
	
	public MWMappingDescriptor getOwningDescriptor() {
		return null; //TODO grrrr, need to make a sepearate interface for the descriptor caching policy for this and adjustRuntimeDescriptor
	}
	
	public boolean usesProjectDefaultCacheSize() {
		throw new UnsupportedOperationException();
	}
	
	public void descriptorInheritanceChanged() {
	}
	
	//***************** TopLink only methods ****************
		
	public MWCachingPolicy getPersistedPolicy() {
		return this;
	}

}
