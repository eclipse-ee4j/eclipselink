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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test predefined queries.
 *
 */
public class PredefinedQueryReadAllTest extends ReadAllTest {
    public PredefinedQueryReadAllTest(Class<?> referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
        setName("PredefinedQueryReadAllTest");
    }

    @Override
    protected void setup() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        setQuery(query);

        getSession().removeQuery("getAllEmployees");
        getSession().addQuery("getAllEmployees", query);

    }

    @Override
    protected void test() {
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployees");
        // Test execution twice to ensure query is cloned correctly
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployees");
    }
}
