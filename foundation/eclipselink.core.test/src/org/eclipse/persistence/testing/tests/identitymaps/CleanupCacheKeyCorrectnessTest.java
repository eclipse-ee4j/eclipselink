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

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.sessions.*;

/**
 * Bug 5840635
 * Ensure that adding a new CacheKey with a null object reference to 
 * SoftCacheWeakIdentityMap during a phase of putting objects into the 
 * IdentityMap does not result in the existing IdentityMap entries getting removed.
 * @author David Minsky
 */
public class CleanupCacheKeyCorrectnessTest extends TestCase {

    protected Class originalIdentityMapClass;
    protected Class newIdentityMapClass;
    protected int originalIdentityMapSize;
    protected int newIdentityMapSize;
    protected int numberOfObjectsToCreate;
    protected int objectsNotFoundInIdentityMap;    

    public CleanupCacheKeyCorrectnessTest() {
        super();
        setDescription("This test verifies that CacheKeys with null object references aren't removed when calling IdentityMap.put()");
    }
    
    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();
        
        newIdentityMapClass = SoftCacheWeakIdentityMap.class;
        newIdentityMapSize = 5;
        
        objectsNotFoundInIdentityMap = 0;
        numberOfObjectsToCreate = newIdentityMapSize * 5;
       
        getSession().getDescriptor(Employee.class).setIdentityMapClass(newIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(newIdentityMapSize);
        
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    /**
     * 1. Create and register multiple Employees with a UoW
     * 2. Commit UoW
     * 3. Check that the employees are contained in the parent session's
     *    IdentityMap; If they are not contained, increment the failure count
     */

    public void test() {
        List employees = new ArrayList<Employee>(numberOfObjectsToCreate);

        UnitOfWork uow = getSession().acquireUnitOfWork();        
        for (int i = 0; i < numberOfObjectsToCreate; i++) {
            int identifier = i + 1;
            Employee employee = new Employee();
            employees.add(employee); // add the original to the list for testing later
            employee.setFirstName("Bob");
            employee.setLastName("Jones#" + identifier);
            uow.registerObject(employee);
        }
        uow.commit();
        
        for (Iterator<Employee> iter = employees.iterator(); iter.hasNext();) {
            Employee employee = iter.next();
            // if the IdentityMap does not contain the employee object, increment failure count
            if (!getSession().getIdentityMapAccessor().containsObjectInIdentityMap(employee)) {
                objectsNotFoundInIdentityMap++;
            }
        }
    }
    
    public void verify() {
        if (objectsNotFoundInIdentityMap > 0) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("The IdentityMap - size(");
            buffer.append(newIdentityMapSize);
            buffer.append(") removed ");
            buffer.append(objectsNotFoundInIdentityMap);
            buffer.append(" new objects out of ");
            buffer.append(numberOfObjectsToCreate);
            buffer.append(" objects added. No objects should have been removed.");
            throw new TestErrorException(buffer.toString());
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

}
