/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
