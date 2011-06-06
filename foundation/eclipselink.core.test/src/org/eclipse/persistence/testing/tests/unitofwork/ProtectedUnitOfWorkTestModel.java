/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - initial creation of this test
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class ProtectedUnitOfWorkTestModel extends UnitOfWorkClientSessionTestModel {
    
    protected boolean hasIsolatedClasses = false;
    
    public void reset() {
        getSession().getProject().setHasIsolatedClasses(this.hasIsolatedClasses);
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        descriptor.setCacheIsolation(CacheIsolationType.SHARED);
        descriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_AFTER_TRANSACTION);
        super.reset();
    }

    public void setup() {
        this.hasIsolatedClasses = getSession().getProject().hasIsolatedClasses();
        getSession().getProject().setHasIsolatedClasses(true);
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        descriptor.setCacheIsolation(CacheIsolationType.PROTECTED);
        descriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_FROM_CLIENT_SESSION);
        super.setup();
    }


}
