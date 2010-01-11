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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class DeletingFromParentSessionTest extends DeleteObjectTest {
    public Address address;

    public DeletingFromParentSessionTest() {
        setDescription("This test deletes an object from a unit of work, and checks to make sure it is removed from the parent session's cache.");
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        originalObject = 
                uow.readObject(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").equal("John"));
        this.address = ((Employee)originalObject).getAddress();
        // Instantiate stuff
        ((Employee)originalObject).getManagedEmployees();
        ((Employee)originalObject).getPhoneNumbers();
        for (java.util.Enumeration mgdEnum = ((Employee)originalObject).getManagedEmployees().elements(); 
             mgdEnum.hasMoreElements(); ) {
            ((Employee)mgdEnum.nextElement()).setManager(null);
        }
        for (java.util.Enumeration mgdProjEnum = ((Employee)originalObject).getProjects().elements(); 
             mgdProjEnum.hasMoreElements(); ) {
            ((org.eclipse.persistence.testing.models.employee.domain.Project)mgdProjEnum.nextElement()).setTeamLeader(null);
        }

        uow.deleteObject(originalObject);
        uow.commit();
    }

    public void verify() {
        Vector primaryKey = getSession().keyFromObject(this.originalObject);
        if (getSession().getIdentityMapAccessor().containsObjectInIdentityMap(primaryKey, 
                                                                              this.originalObject.getClass())) {
            throw new TestException("The object " + originalObject + 
                                    " was deleted from the unit of work cache, but " + 
                                    "not the parent session's cache.");
        }

        primaryKey = getSession().keyFromObject(this.address);
        if (getSession().getIdentityMapAccessor().containsObjectInIdentityMap(primaryKey, 
                                                                              this.address.getClass())) {
            throw new TestException("The object " + this.address + 
                                    " was deleted from the unit of work cache, but " + 
                                    "not the parent session's cache.");
        }
    }
}
