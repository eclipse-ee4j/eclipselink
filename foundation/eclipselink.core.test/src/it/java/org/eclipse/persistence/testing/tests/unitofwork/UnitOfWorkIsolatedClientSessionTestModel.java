/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Iterator;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;


/**
 * This model is used to test the unit of work on an isolated client/server session.
 */
public class UnitOfWorkIsolatedClientSessionTestModel extends UnitOfWorkClientSessionTestModel {

    public void addTests() {
        addTest(new UnitOfWorkTestSuite());
        // No protected tests if all entities are isolated
        // bug 3128227
        addTest(new UnitOfWorkRollbackConnectionReleaseTest());
    }

    public void setup() {
        for (Iterator descriptors = getSession().getDescriptors().values().iterator(); descriptors.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setCacheIsolation(CacheIsolationType.ISOLATED);
        }
        getSession().getProject().setHasIsolatedClasses(true);
        super.setup();
    }

    public void reset() {
        for (Iterator descriptors = getSession().getDescriptors().values().iterator(); descriptors.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setCacheIsolation(CacheIsolationType.SHARED);
        }
        getSession().getProject().setHasIsolatedClasses(false);
        super.reset();
    }
}
