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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;

final class MWNullCachingPolicy extends MWModel
											implements MWCachingPolicy
{
	MWNullCachingPolicy(MWModel parent)
	{
		super(parent);
	}
	
	public void initializeFrom(MWCachingPolicy otherPolicy) {
		throw new UnsupportedOperationException();
	}

	public ExistenceCheckingOption getExistenceChecking() {
		return null;
	}

	public CacheTypeOption getCacheType() {
		return null;
	}

	public int getCacheSize() {
		return 0;
	}

	public void setExistenceChecking(ExistenceCheckingOption newExistenceChecking) {
		throw new UnsupportedOperationException("Null policy cannot be modified.");
	}
    
    public void setExistenceChecking(String existenceChecking) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

	public void setCacheType(CacheTypeOption cacheType) {
		throw new UnsupportedOperationException("Null policy cannot be modified.");
	}
	
	public void setCacheType(String cacheTypeString) {
		throw new UnsupportedOperationException("Can not modify a null policy");
	}

	public void setCacheSize(int size) {
		throw new UnsupportedOperationException("Null policy cannot be modified.");
	}

    public MWCacheExpiry getCacheExpiry() {
        return null;
    }
    
    public void setUseProjectDefaultCacheExpiry(boolean useProjectCacheExpiry) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }
    
	public CacheCoordinationOption getCacheCoordination() {
		return null;
	}

	public void setCacheCoordination(CacheCoordinationOption cacheCoordination) {
		throw new UnsupportedOperationException("Null policy cannot be modified.");
	}

	public CacheIsolationOption getCacheIsolation() {
		return null;
	}

	public void setCacheIsolation(CacheIsolationOption cacheIsolation) {
		throw new UnsupportedOperationException("Null policy cannot be modified.");
	}
	
	public MWMappingDescriptor getOwningDescriptor() {
		throw new UnsupportedOperationException();//TODO grrrr, need to make a separate interface for the descriptor caching policy for this and adjustRuntimeDescriptor
	}
	
	public boolean usesProjectDefaultCacheSize() {
		throw new UnsupportedOperationException();
	}	
	
	public void descriptorInheritanceChanged() {
	}
	

	
	// ***************** runtime conversion ***************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor)	 {
		//nothing here
	}

	
	public MWCachingPolicy getPersistedPolicy() {
		return null;
	}
}
