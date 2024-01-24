/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;


/**
 * This model is used to test the unit of work on an isolated client/server session.
 */
public class UnitOfWorkIsolatedClientSessionTestModel extends UnitOfWorkClientSessionTestModel {

    @Override
    public void addTests() {
        addTest(new UnitOfWorkTestSuite());
        // No protected tests if all entities are isolated
        // bug 3128227
        addTest(new UnitOfWorkRollbackConnectionReleaseTest());
    }

    @Override
    public void setup() {
        for (ClassDescriptor descriptor : getSession().getDescriptors().values()) {
            descriptor.setCacheIsolation(CacheIsolationType.ISOLATED);
        }
        getSession().getProject().setHasIsolatedClasses(true);
        super.setup();
    }

    @Override
    public void reset() {
        for (ClassDescriptor descriptor : getSession().getDescriptors().values()) {
            descriptor.setCacheIsolation(CacheIsolationType.SHARED);
        }
        getSession().getProject().setHasIsolatedClasses(false);
        super.reset();
    }
}
