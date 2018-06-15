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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests an invalid bath attribute set on a query. Ensures no NPE is caught
 * and that the correct validation exception is thrown.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date October 5, 2004
 */
public class BatchReadingWithInvalidQueryKeyTest extends TestCase {
    private ValidationException m_validationException;
    private int m_expectedErrorCode = ValidationException.MISSING_MAPPING;
    BatchFetchType batchType;

    public BatchReadingWithInvalidQueryKeyTest(BatchFetchType batchType) {
        setDescription("Tests an invalid batch attribute set on a query.");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void test() {
        // Pull the objects into the cache first. For the test to fail
        // the object must already be in the cache.
        getSession().readAllObjects(Restaurant.class);

        ReadAllQuery query = new ReadAllQuery();
        query.setBatchFetchType(batchType);
        query.setReferenceClass(Restaurant.class);
        query.addBatchReadAttribute("I_must_surely_not_exist");

        try {
            getSession().executeQuery(query);
        } catch (ValidationException e) {
            m_validationException = e;
        }
    }

    public void verify() {
        if (m_validationException == null) {
            throw new TestErrorException("No exception was caught on the invalid query key");
        } else if (m_validationException.getErrorCode() != m_expectedErrorCode) {
            throw new TestErrorException("Incorrect validation exception was caught (" + m_validationException.getErrorCode() + ") was expecting (" + m_expectedErrorCode + ")", m_validationException);
        }
    }
}
