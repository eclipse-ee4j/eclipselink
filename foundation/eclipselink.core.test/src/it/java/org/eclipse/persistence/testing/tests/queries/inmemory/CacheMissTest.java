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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * Test for cache on 1-1 traversal.
 *
 */
public class CacheMissTest extends CacheHitTest {
    protected Employee employee;

    public CacheMissTest() {
        setDescription("Test cache miss for non-primary key query.");
        originalObject = PopulationManager.getDefaultManager().getObject(Employee.class, "0001");
    }

    /**
     * Query the object by primary key.
     */
    @Override
    protected Object readObject() {
        return getSession().readObject(Employee.class, new ExpressionBuilder().get("id").notEqual(((Employee)objectToRead).getId()));
    }

    @Override
    protected void verify() {
        if (objectRead == objectToRead) {
            throw new TestErrorException("Object read match but should not.");
        }

        if (tempStream.toString().isEmpty()) {
            throw new TestErrorException("Cache hit occurred, but should not have.");
        }
    }
}
