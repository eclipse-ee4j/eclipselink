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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test to make sure the reference cache of HardCacheWeakIdentityMap is properly maintained.
 * Although the way the maintenance takes place will be different for JDK 1.3 and JDK 1.4,
 * the end result should be the same.
 */
public class HardCacheWeakIdentityMapTest extends TestCase {
    protected Class originalIdentityMapClass = null;
    protected int originalIdentityMapSize = 0;
    protected Employee firstEmployee = null;
    protected Employee secondEmployee = null;
    protected boolean firstEmployeeDropped = false;
    protected boolean referenceCacheSizeMaintained = false;
    public static final int REFERENCE_CACHE_SIZE = 10;

    public HardCacheWeakIdentityMapTest() {
    }

    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();
        getSession().getDescriptor(Employee.class).setIdentityMapClass(HardCacheWeakIdentityMap.class);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(REFERENCE_CACHE_SIZE);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        // insert and store two employees in a predictable order
        UnitOfWork uow = getSession().acquireUnitOfWork();
        firstEmployee = new Employee();
        firstEmployee.setFirstName("Test0");
        firstEmployee.setLastName("Employee");
        uow.registerObject(firstEmployee);
        uow.commit();

        uow = getSession().acquireUnitOfWork();
        Employee secondEmployee = new Employee();
        secondEmployee.setFirstName("Test1");
        secondEmployee.setLastName("Employee");
        uow.registerObject(secondEmployee);
        uow.commit();

        // insert 9 more employees to fill the subcache
        for (int i = 2; i < (REFERENCE_CACHE_SIZE + 1); i++) {
            uow = getSession().acquireUnitOfWork();
            Employee employee = (Employee)uow.registerObject(new Employee());
            employee.setFirstName("Test" + i);
            employee.setLastName("Employee");
            uow.commit();
        }

        HardCacheWeakIdentityMap map = (HardCacheWeakIdentityMap)getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        referenceCacheSizeMaintained = (map.getReferenceCache().size() == REFERENCE_CACHE_SIZE);
        firstEmployeeDropped = !map.getReferenceCache().contains(firstEmployee);

        // query the first employee to put him back in the sub cache
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder employee = new ExpressionBuilder();
        Expression exp = employee.get("firstName").equal("Test0");
        query.setSelectionCriteria(exp);
        Employee queryResult = (Employee)getSession().executeQuery(query);

        referenceCacheSizeMaintained = (referenceCacheSizeMaintained && (map.getReferenceCache().size() == REFERENCE_CACHE_SIZE));
    }

    public void verify() {
        HardCacheWeakIdentityMap map = (HardCacheWeakIdentityMap)getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);

        if (!referenceCacheSizeMaintained) {
            throw new TestErrorException("The HardCacheWeakIdentityMap sub-cache was not correctly maintained.  The reference cache size" + " was not correctly maintained.");
        }

        if (!firstEmployeeDropped) {
            throw new TestErrorException("The HardCacheWeakIdentityMap sub-cache was not correctly maintained.  An object was not" + " correctly dropped when the sub-cache was filled.");
        }

        if (!map.getReferenceCache().contains(firstEmployee)) {
            throw new TestErrorException("The HardCacheWeakIdentityMap sub-cache was not correctly maintained.  An object was not" + " correctly replaced when reread after being removed.");

        }

        if (map.getReferenceCache().contains(secondEmployee)) {
            throw new TestErrorException("The HardCacheWeakIdentityMap sub-cache was not correctly maintained.  An object was not" + " correctly removed when an object was reread.");
        }
    }
}
