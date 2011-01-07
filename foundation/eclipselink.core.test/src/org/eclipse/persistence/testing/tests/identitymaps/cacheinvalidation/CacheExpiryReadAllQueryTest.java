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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test ReadAllQuery with Cache Expiry
 * This test can test both queries that will return valid objects and queries that will return
 * invalid objects.
 */
public class CacheExpiryReadAllQueryTest extends CacheExpiryTest {

    protected boolean shouldExpire = false;
    protected Vector employeeNames = null;
    protected Vector queriedEmployees = null;

    public CacheExpiryReadAllQueryTest(boolean shouldExpire) {
        setDescription("Test Cache Expiry a read Object Query.");
        this.shouldExpire = shouldExpire;
    }

    public void setup() {
        super.setup();
        // Use time to live expire that create a life long enough for this test to complete
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(100000));
        employeeNames = new Vector();
    }

    public void test() {
        // Get the names of all the employees
        Enumeration employees = getSession().readAllObjects(Employee.class).elements();
        while (employees.hasMoreElements()) {
            employeeNames.addElement(((Employee)employees.nextElement()).getFirstName());
        }

        // Changed the names of all the employees in the cache
        Enumeration employeeMap = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(Employee.class).keys();
        while (employeeMap.hasMoreElements()) {
            Employee employee = (Employee)((CacheKey)employeeMap.nextElement()).getObject();
            employee.setFirstName(employee.getFirstName() + "-mutated");
        }

        // if these employees should expire, invalidate them.
        if (shouldExpire) {
            getAbstractSession().getIdentityMapAccessor().invalidateClass(Employee.class);
        }

        // Read the employees back.  If the employees expired, the original names will come back
        // with the employees.  If the employees did not expire, the mutated names will be in come back
        // from the cache.
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        queriedEmployees = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        Enumeration queriedResults = queriedEmployees.elements();
        while (queriedResults.hasMoreElements()) {
            Employee employee = (Employee)queriedResults.nextElement();
            if (shouldExpire && !employeeNames.contains(employee.getFirstName())) {
                throw new TestErrorException("Expiring Read All Query did not properly cache results.  Please ensure this system is not running " + 
                                             "with a heavy load prior to filing a bug since the cache expiry tests rely to a certain degree on timing.");
            } else if (!shouldExpire && employeeNames.contains(employee.getFirstName())) {
                throw new TestErrorException("Non-Expiring Read All Query did not properly cache results.  Please ensure this system is not running " + 
                                             "with a heavy load prior to filing a bug since the cache expiry tests rely to a certain degree on timing.");
            }
            employeeNames.remove(employee.getFirstName());
        }
    }
}
