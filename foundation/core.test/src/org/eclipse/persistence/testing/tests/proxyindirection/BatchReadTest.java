/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.List;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;

public class BatchReadTest extends TestCase {
    List employees;

    public BatchReadTest() {
        setDescription("Tests ReadAllObjects using Proxy Indirection.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addBatchReadAttribute("address");

        employees = (List)getSession().executeQuery(query);
        ((Employee)employees.get(0)).getAddress();
    }
}
