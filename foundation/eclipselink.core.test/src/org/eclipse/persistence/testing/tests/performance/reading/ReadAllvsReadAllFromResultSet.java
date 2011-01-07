/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.reading;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.framework.*;

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

    public void setup() {
        query = new ReadAllQuery(Address.class);
        resultSetQuery = new ReadAllQuery(Address.class);
        resultSetQuery.setIsResultSetOptimizedQuery(true);
        //getSession().getDescriptor(Address.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
    }

    public void reset() throws Exception {
        //getSession().getDescriptor(Address.class).setObjectChangePolicy(new DeferredChangeDetectionPolicy());
    }

    /**
     * Read all address.
     */
    public void test() throws Exception {
        UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
        unitOfWork.beginEarlyTransaction();
        List results = (List)unitOfWork.executeQuery(query);
        unitOfWork.release();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public PerformanceComparisonTestCase readAllFromResultSet() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
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
