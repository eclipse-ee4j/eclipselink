/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     GYORKE - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.MethodBaseQueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 *
 */
public class DoNotRedirectDefaultRedirectorTest extends TestCase {

    protected ClassDescriptor descriptor = null;

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        descriptor = getSession().getProject().getDescriptors().get(Employee.class);

        descriptor.setDefaultReadObjectQueryRedirector(new MethodBaseQueryRedirector(DoNotRedirectDefaultRedirectorTest.class, "readObject"));
    }

    @Override
    public void test() {
        // test readAll
        ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
        roq.setSelectionId(99);
        roq.setDoNotRedirect(true);
        try {
            Employee employee = (Employee)getSession().executeQuery(roq);
        } catch (Exception exc) {
            throw new TestErrorException("DoNotRedirect was ignored");
        }

    }

    @Override
    public void verify() {
    }

    @Override
    public void reset() {
        descriptor.setDefaultReadObjectQueryRedirector(null);
    }

    /**
     * Below are the methods called by the redirectors for various toplink queries
     */

    public static Object readObject(DatabaseQuery query, DataRecord row, org.eclipse.persistence.sessions.Session session) {
        throw new TestErrorException("Query setting doNotRedirect was ignored");
    }

}
