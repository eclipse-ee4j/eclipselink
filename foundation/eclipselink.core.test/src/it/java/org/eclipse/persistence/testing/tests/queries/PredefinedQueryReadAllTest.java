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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadAllTest;

/**
 * Test predefined queries.
 *
 */
public class PredefinedQueryReadAllTest extends ReadAllTest {
    public PredefinedQueryReadAllTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
        setName("PredefinedQueryReadAllTest");
    }

    protected void setup() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        setQuery(query);

        getSession().removeQuery("getAllEmployees");
        getSession().addQuery("getAllEmployees", query);

    }

    protected void test() {
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployees");
        // Test execution twice to ensure query is cloned correctly
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployees");
    }
}
