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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.PerformanceComparisonTestCase;
import org.eclipse.persistence.testing.models.performance.Address;

import java.util.List;

/**
 * This test compares the performance of read all vs reading directly from a result-set.
 */
public class ReadAllvsReadAllFromResultSet extends PerformanceComparisonTestCase {
    protected ReadAllQuery query;
    protected ReadAllQuery resultSetQuery;

    public ReadAllvsReadAllFromResultSet() {
        setDescription("This test compares the performance of read all vs reading directly from a result-set.");
        addTest(readAllFromResultSet());
    }

    @Override
    public void setup() {
        query = new ReadAllQuery(Address.class);
        resultSetQuery = new ReadAllQuery(Address.class);
        resultSetQuery.setIsResultSetOptimizedQuery(true);
        //getSession().getDescriptor(Address.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
    }

    @Override
    public void reset() throws Exception {
        //getSession().getDescriptor(Address.class).setObjectChangePolicy(new DeferredChangeDetectionPolicy());
    }

    /**
     * Read all address.
     */
    @Override
    public void test() throws Exception {
        UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
        unitOfWork.beginEarlyTransaction();
        List results = (List)unitOfWork.executeQuery(query);
        unitOfWork.release();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public PerformanceComparisonTestCase readAllFromResultSet() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void test() {
                UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
                unitOfWork.beginEarlyTransaction();
                List results = (List)unitOfWork.executeQuery(resultSetQuery);
                unitOfWork.release();
                getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            }
        };
        test.setName("ReadAllFromResultSet");
        return test;
    }
}
