/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test ReadObject Query with Cache Expiry.
 * This test will work for both expiring employees and non-expiring employees.
 */
public class CacheExpiryReadObjectQueryTest extends CacheExpiryTest {

    protected boolean shouldExpire = false;
    protected Employee employee = null;
    protected String firstName = null;
    protected Employee queriedEmployee = null;
    protected ReadObjectQuery query = null;

    public CacheExpiryReadObjectQueryTest(Employee employee, ReadObjectQuery query, boolean shouldExpire) {
        setDescription("Test Cache Expiry a read Object Query.");
        this.employee = employee;
        this.shouldExpire = shouldExpire;
        this.query = query;
    }

    public void setup() {
        super.setup();
        // Use cache expiry which will last the during of this test.
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(100000));
    }

    public void test() {
        // read an employee and then mutate the first name in the cache.
        Employee readEmployee = (Employee)getSession().readObject(employee);
        firstName = readEmployee.getFirstName();
        Employee mutatedEmployee = (Employee)((AbstractSession)getSession()).getIdentityMapAccessorInstance().getFromIdentityMap(employee);
        mutatedEmployee.setFirstName(firstName + "-mutated");

        // invalidate the employee in the cache if neccessary
        if (shouldExpire) {
            getAbstractSession().getIdentityMapAccessor().invalidateObject(mutatedEmployee);
        }

        // Reread the employee.  If the employee expired, we will get an employee with the original 
        // name.  Otherwise we will get an employee with the mutated name.
        queriedEmployee = (Employee)getSession().executeQuery(query);
    }

    public void verify() {
        if (shouldExpire && !firstName.equals(queriedEmployee.getFirstName())) {
            throw new TestErrorException("Expiring Read Object Query did not properly expire.  Please ensure this system is not running " + 
                                         "with a heavy load prior to filing a bug since the cache expiry tests rely to a certain degree on timing.");

        } else if (!shouldExpire && firstName.equals(queriedEmployee.getFirstName())) {
            throw new TestErrorException("Non-Expiring Read Object Query did not properly cache results.  Please ensure this system is not running " + 
                                         "with a heavy load prior to filing a bug since the cache expiry tests rely to a certain degree on timing.");

        }

    }
}
