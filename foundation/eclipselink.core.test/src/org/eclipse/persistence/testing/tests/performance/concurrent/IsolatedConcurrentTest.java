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
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of read all.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public abstract class IsolatedConcurrentTest extends ConcurrentPerformanceComparisonTest {

    /**
     * Switch the descriptors to isolated.
     * Find all employees.
     */
    public void setup() {
        super.setup();
        for (Iterator descriptors = getServerSession().getDescriptors().values().iterator();
                 descriptors.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setIsIsolated(true);
        }
        getServerSession().getProject().setHasIsolatedClasses(true);
    }

    /**
     * Switch the descriptors back.
     */
    public void reset() {
        super.reset();
        for (Iterator descriptors = getServerSession().getDescriptors().values().iterator();
                 descriptors.hasNext();) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            descriptor.setIsIsolated(false);
        }
        getServerSession().getProject().setHasIsolatedClasses(false);
    }
}
