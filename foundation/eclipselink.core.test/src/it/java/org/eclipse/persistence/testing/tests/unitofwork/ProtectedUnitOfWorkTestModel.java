/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Gordon Yorke - initial creation of this test
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
