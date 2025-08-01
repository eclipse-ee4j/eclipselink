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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Vector;

public class RefreshQueryCacheExpiryTest extends CacheExpiryTest {

    protected Vector employees = null;
    protected Vector employeeNames = null;


    public RefreshQueryCacheExpiryTest() {
        setDescription("Ensure refresh queries correcly refresh both the object and the expiry time");
    }

    @Override
    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000));
        employees = getSession().readAllObjects(Employee.class);
        employeeNames = new Vector();
    }

    @Override
    public void test() {
        Enumeration empEnum = employees.elements();
        while (empEnum.hasMoreElements()) {
            CacheKey key = getAbstractSession().getIdentityMapAccessorInstance().getCacheKeyForObject(empEnum.nextElement());
            employeeNames.add(((Employee)key.getObject()).getFirstName());
            ((Employee)key.getObject()).setFirstName(((Employee)key.getObject()).getFirstName() + "-mutated");
        }
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.refreshIdentityMapResult();
        employees = (Vector)getSession().executeQuery(query);
    }

    @Override
    public void verify() {
        Enumeration empEnum = employees.elements();
        while (empEnum.hasMoreElements()) {
            CacheKey key = getAbstractSession().getIdentityMapAccessorInstance().getCacheKeyForObject(empEnum.nextElement());
            if (!employeeNames.contains(((Employee)key.getObject()).getFirstName())) {
                throw new TestErrorException("Refreshing ReadAllQuery does not properly refresh data when used with Cache Expiry.");
            }
            employeeNames.remove(((Employee)key.getObject()).getFirstName());
        }
        if (!employeeNames.isEmpty()) {
            throw new TestErrorException("Not all values were returned from the refreshing ReadAllQuery when used with Cache Expiry.");
        }
    }
}
