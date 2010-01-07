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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Model;


import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Inteface describing the base implementation for a descriptor caching
 * policy.
 * 
 * @version 10.1.3
 */
public interface MWCachingPolicy extends MWNode, Model
{
	// Cache Type
	String CACHE_TYPE_FULL = "Full";
	String CACHE_TYPE_WEAK_WITH_HARD_SUBCACHE = "Weak with Hard Subcache" ;
	String CACHE_TYPE_NONE = "None";
	String CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE = "Weak with Soft subcache";
	String CACHE_TYPE_WEAK = "Weak";
	String CACHE_TYPE_SOFT = "Soft";
	String CACHE_TYPE_PROJECT_DEFAULT = "DEFAULT"; // Will use project value
	String DEFAULT_CACHE_TYPE = CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE;

	CacheTypeOption getCacheType();
	void setCacheType(CacheTypeOption cacheType);
	void setCacheType(String cacheType);
		String CACHE_TYPE_PROPERTY = "cacheType";

	// Caching Size
	int DEFAULT_CACHE_SIZE = 100;

	int getCacheSize();
	void setCacheSize(int size);
		String CACHE_SIZE_PROPERTY = "cacheSize";

	boolean usesProjectDefaultCacheSize();
	
	// Cache Coordination
	String CACHE_COORDINATION_SYNCHRONIZE_CHANGES = "Synchronize Changes";
	String CACHE_COORDINATION_NONE = "None";
	String CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS = "Synchronize Changes and New Objects";
	String CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS = "Invalidate Changed Objects";
	String CACHE_COORDINATION_PROJECT_DEFAULT = "DEFAULT"; // Will use project value
	String DEFAULT_CACHE_COORDINATION = CACHE_COORDINATION_SYNCHRONIZE_CHANGES;

    CacheCoordinationOption getCacheCoordination();
	void setCacheCoordination(CacheCoordinationOption cacheCoordination);
		String CACHE_COORDINATION_PROPERTY = "cacheCoordination";

	// Cache Isolation
	String CACHE_ISOLATION_SHARED = "Shared";
	String CACHE_ISOLATION_ISOLATED = "Isolated";
	String CACHE_ISOLATION_PROJECT_DEFAULT = "DEFAULT"; // Will use project value
	String DEFAULT_CACHE_ISOLATION = CACHE_ISOLATION_SHARED;

    CacheIsolationOption getCacheIsolation();
	void setCacheIsolation(CacheIsolationOption cacheIsolation);
		String CACHE_ISOLATION_PROPERTY = "cacheIsolation";

	// Existence Checking types
	String EXISTENCE_CHECKING_CHECK_CACHE = "Check cache";
	String EXISTENCE_CHECKING_CHECK_DATABASE = "Check database";
	String EXISTENCE_CHECKING_ASSUME_EXISTENCE = "Assume existence";
	String EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE = "Assume non-existence";
	String EXISTENCE_CHECKING_PROJECT_DEFAULT = "DEFAULT"; // Will use project value
	String DEFAULT_EXISTENCE_CHECKING = EXISTENCE_CHECKING_CHECK_CACHE;

    ExistenceCheckingOption getExistenceChecking();
    void setExistenceChecking(ExistenceCheckingOption existenceChecking);
    void setExistenceChecking(String existenceChecking);
		String EXISTENCE_CHECKING_PROPERTY = "existenceChecking";
	
    MWCacheExpiry getCacheExpiry();
    void setUseProjectDefaultCacheExpiry(boolean useProjectCacheExpiry);
    
	
	MWMappingDescriptor getOwningDescriptor();
	
	void initializeFrom(MWCachingPolicy otherPolicy);
	
	void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor);
	
	MWCachingPolicy getPersistedPolicy();
	
	void descriptorInheritanceChanged();

	public static class CacheTypeOption extends TopLinkOption { 

		public CacheTypeOption(String mwModelString, String externalString, String toplinkClassName) {
			super(mwModelString, externalString, toplinkClassName);
		}
					
		public void setMWOptionOnTopLinkObject(Object descriptor) {
            if (getMWModelOption() == MWCachingPolicy.CACHE_TYPE_PROJECT_DEFAULT) {
                throw new IllegalStateException("Cannot convert the project default, handle this first");
            }
		    ((ClassDescriptor) descriptor).setIdentityMapClass(ClassTools.classForName((String)getTopLinkModelOption()));	
		}
	}
    
    public static class CacheIsolationOption extends TopLinkOption { 

        public CacheIsolationOption(String mwModelString, String externalString) {
            super(mwModelString, externalString);
        }
                    
        public void setMWOptionOnTopLinkObject(Object descriptor) {
            if (getMWModelOption() == MWCachingPolicy.CACHE_ISOLATION_PROJECT_DEFAULT) {
                throw new IllegalStateException("Cannot convert the project default, handle this first");
            }
           ((ClassDescriptor) descriptor).setIsIsolated(getMWModelOption() == MWCachingPolicy.CACHE_ISOLATION_ISOLATED);
        }
    }

    public static class CacheCoordinationOption extends TopLinkOption { 

        public CacheCoordinationOption(String mwModelString, String externalString, int toplinkCacheSynchronizationType) {
            super(mwModelString, externalString, new Integer(toplinkCacheSynchronizationType));
        }
                    
        public void setMWOptionOnTopLinkObject(Object descriptor) {
            if (getMWModelOption() == MWCachingPolicy.CACHE_COORDINATION_PROJECT_DEFAULT) {
                throw new IllegalStateException("Cannot convert the project default, handle this first");
            }
           ((ClassDescriptor) descriptor).setCacheSynchronizationType(((Integer) getTopLinkModelOption()).intValue());
        }
    }
    
    
    public static class ExistenceCheckingOption extends TopLinkOption { 

        public ExistenceCheckingOption(String mwModelString, String externalString, int toplinkExistenceCheckingOption) {
            super(mwModelString, externalString, new Integer(toplinkExistenceCheckingOption));
        }
                    
        public void setMWOptionOnTopLinkObject(Object descriptor) {
            if (getMWModelOption() == MWCachingPolicy.EXISTENCE_CHECKING_PROJECT_DEFAULT) {
                throw new IllegalStateException("Cannot convert the project default, handle this first");
            }
            ((ClassDescriptor) descriptor).getQueryManager().getDoesExistQuery().setExistencePolicy(((Integer) getTopLinkModelOption()).intValue());
        }
    }
}
