/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test the API Associated with Cache Expiry to ensure it works.
 */
public class CacheExpiryAPITest extends CacheExpiryTest {

    protected String isValidSuccess = null;
    protected boolean invalidateObjectSuccess = true;
    protected boolean invalidateCollectionSuccess = true;
    protected boolean invalidateClassSuccess = true;
    protected long remainingTime = 0;

    public CacheExpiryAPITest() {
        setDescription("Test the specific API for Cache Expiry on the IdentityMapAccessor.");
    }

    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000));
        getSession().readAllObjects(Employee.class);
    }

    public void test() {
        // validate the IdentitityMapAccessor isValid method against the CacheKey.isValidMethod()
        Employee employee = (Employee)getSession().readObject(Employee.class);
        boolean validEmployee = getAbstractSession().getIdentityMapAccessor().isValid(employee);
        if (getValidityFromIdentityMapManager(employee) != validEmployee) {
            isValidSuccess = "identityMapAccessor().isValid() did not return true for a valid object.";
            return;
        }
        ((TimeToLiveCacheInvalidationPolicy)getSession().getDescriptor(Employee.class).getCacheInvalidationPolicy()).setTimeToLive(-1);
        validEmployee = getAbstractSession().getIdentityMapAccessor().isValid(employee);
        if (getValidityFromIdentityMapManager(employee) != validEmployee) {
            isValidSuccess = "identityMapAccessor().isValid() did not return false for an invalid object.";
            return;
        }

        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        getSession().readAllObjects(Employee.class);

        // validate the IdentitityMapAccessor invalidateObject method
        employee = (Employee)getSession().readObject(Employee.class);
        getAbstractSession().getIdentityMapAccessor().invalidateObject(employee);
        if (getAbstractSession().getIdentityMapAccessor().isValid(employee)) {
            invalidateObjectSuccess = false;
            return;
        }

        // validate the IdentitityMapAccessor invalidateObjects method
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("lastName").equal("Smith");
        Vector smiths = getSession().readAllObjects(Employee.class, exp);
        getAbstractSession().getIdentityMapAccessor().invalidateObjects(smiths);
        Enumeration smithEnum = smiths.elements();
        while (smithEnum.hasMoreElements()) {
            employee = (Employee)smithEnum.nextElement();
            if (getAbstractSession().getIdentityMapAccessor().isValid(employee)) {
                invalidateCollectionSuccess = false;
                return;
            }
        }

        // validate the IdentitityMapAccessor invalidateClass method
        Vector allEmployees = getSession().readAllObjects(Employee.class);
        getAbstractSession().getIdentityMapAccessor().invalidateClass(Employee.class);
        Enumeration empEnum = allEmployees.elements();
        while (empEnum.hasMoreElements()) {
            employee = (Employee)empEnum.nextElement();
            if (getAbstractSession().getIdentityMapAccessor().isValid(employee)) {
                invalidateClassSuccess = false;
                return;
            }
        }

        // validate the IdentitityMapAccessor remainingValidTime method. Since we cannot be exactly sure
        // about the timing, we will check if the two method calls occur within one second of one another
        // this should be a small enough time to be meaningful and large enough to allow method calls to
        // occur.
        getAbstractSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(100000));
        getAbstractSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getAbstractSession().readObject(Employee.class);
        remainingTime = getAbstractSession().getIdentityMapAccessor().getRemainingValidTime(employee);
    }

    public void verify() {
        if (isValidSuccess != null) {
            throw new TestErrorException("IsValid() Failed. " + isValidSuccess);
        }
        if (!invalidateObjectSuccess) {
            throw new TestErrorException("Invalidate Object API call did not correctly invalidate objects.");
        }
        if (!invalidateCollectionSuccess) {
            throw new TestErrorException("Invalidate Objects API call did not correctly invalidate objects.");
        }
        if (!invalidateClassSuccess) {
            throw new TestErrorException("Invalidate Class API call did not correctly invalidate objects.");
        }
        if (remainingTime > 101000 || remainingTime < 99000) {
            throw new TestErrorException("Get Remaining Life API call did not return a reasonable value.  Please check to see that the" + 
                                         " the system is not so overloaded that the time between 2 lines of java is greater than 1 second.");
        }

    }

    public boolean getValidityFromIdentityMapManager(Object object) {
        CacheKey key = getAbstractSession().getIdentityMapAccessorInstance().getCacheKeyForObject(object);
        return !getAbstractSession().getDescriptor(object).getCacheInvalidationPolicy().isInvalidated(key, 
                                                                                                                 System.currentTimeMillis());
    }
}
