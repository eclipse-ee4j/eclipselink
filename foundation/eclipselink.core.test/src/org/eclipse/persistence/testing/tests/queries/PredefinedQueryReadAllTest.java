/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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