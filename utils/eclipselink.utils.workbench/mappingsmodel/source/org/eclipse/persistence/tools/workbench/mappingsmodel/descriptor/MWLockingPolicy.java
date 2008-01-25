/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.utility.Model;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public interface MWLockingPolicy extends MWNode, Model {
    
    String getLockingType();
    void setLockingType(String newLockingType);
        String LOCKING_TYPE_PROPERTY = "lockingType";
        // locking types
        String OPTIMISTIC_LOCKING = "Optimistic Locking";
        String PESSIMISTIC_LOCKING = "Pessimistic Locking";
        String NO_LOCKING = "None"; 
        String DEFAULT_LOCKING_TYPE = NO_LOCKING;
 

    MWDataField getVersionLockField();
    void setVersionLockField(MWDataField newLockField);
  
    boolean shouldStoreVersionInCache();
    void setStoreInCache(boolean newStoreInCache);
        public final static String STORE_IN_CACHE_PROPERTY = "storeInCache";

    
        void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor);

}
