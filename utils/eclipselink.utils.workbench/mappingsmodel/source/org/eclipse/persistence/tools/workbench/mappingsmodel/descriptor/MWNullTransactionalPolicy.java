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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public final class MWNullTransactionalPolicy extends MWModel 
	implements MWTransactionalPolicy 
{

    private MWLockingPolicy lockingPolicy = new MWNullLockingPolicy(this);
    private MWCachingPolicy cachingPolicy = new MWNullCachingPolicy(this);
    
	// ********** Constructors **********

	MWNullTransactionalPolicy(MWModel parent) {
		super(parent);
	}

	public MWQueryManager getQueryManager() {
		return null;
	}

	public MWRefreshCachePolicy getRefreshCachePolicy() {
		return null;
	}

	public MWCachingPolicy getCachingPolicy() {
		return this.cachingPolicy;
	}
	
	public void setCachingPolicy(MWCachingPolicy cachingPolicy) {
		throw new UnsupportedOperationException("Cannot modify a Null Transactional Policy");
	}
	
    public MWLockingPolicy getLockingPolicy() {
        return this.lockingPolicy;
    }
    
	public boolean isConformResultsInUnitOfWork() {
		return false;
	}

	public void setConformResultsInUnitOfWork(boolean conform){
		throw new UnsupportedOperationException("Cannot modify a Null Transactional Policy");
	}

	public boolean isReadOnly() {
		return false;
	}

	public void setReadOnly(boolean newValue) {
		throw new UnsupportedOperationException("A non-transactional descriptor cannot set the readOnly property");
	}
	
	public void descriptorInheritanceChanged() {
		// no op
	}
	
    public String getDescriptorAlias() {
        return null;
    }
    
    public void setDescriptorAlias(String descriptorAlias) {
        throw new UnsupportedOperationException("Cannot modify a Null Transactional Policy");        
    }
    
	// ************ Runtime Conversion ***********
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {

	}


	// ************ TopLink Methods ***********
	
	public MWAbstractTransactionalPolicy getValueForTopLink() {
		return null;
	}
    
}
