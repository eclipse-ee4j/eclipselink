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
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.Iterator;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.ConcurrentPerformanceRegressionTest;

/**
 * This test compares the concurrency of read all.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public abstract class IsolatedConcurrentRegressionTest extends ConcurrentPerformanceRegressionTest {

    /**
     * Switch the descriptors to isolated.
     * Find all employees.
     */
    @Override
    public void setup() {
        super.setup();
        for (Iterator descriptors = getServerSession().getDescriptors().values().iterator();
                 descriptors.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setCacheIsolation(CacheIsolationType.ISOLATED);
        }
        getServerSession().getProject().setHasIsolatedClasses(true);
    }

    /**
     * Switch the descriptors back.
     */
    @Override
    public void reset() {
        super.reset();
        for (Iterator descriptors = getServerSession().getDescriptors().values().iterator();
                 descriptors.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setCacheIsolation(CacheIsolationType.SHARED);
        }
        getServerSession().getProject().setHasIsolatedClasses(false);
    }
}
