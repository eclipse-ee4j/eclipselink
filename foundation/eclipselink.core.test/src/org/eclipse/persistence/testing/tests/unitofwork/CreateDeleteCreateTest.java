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

import java.math.BigDecimal;

import java.util.Vector;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * <b>Purpose:</b>Tests creating, deleting, and then recreating the same
 * new object in a unit of work.
 * <p>
 * The second create is a new object with the same primary key.
 */
public class CreateDeleteCreateTest extends AutoVerifyTestCase {
    Vector result = null;
    UnitOfWork uow = null;
    Employee hizungClone = null;

    public CreateDeleteCreateTest() {
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        uow.setShouldNewObjectsBeCached(true);
    }

    protected void test() {
        Employee mary = new Employee();
        mary.setFirstName("Mary");
        mary.setLastName("Magdalene");
        mary.setId(new BigDecimal(694803));

        Employee maryClone = (Employee)uow.registerObject(mary);

        uow.deleteObject(maryClone);

        Employee hizung = mary;
        hizung.setFirstName("Hizung");
        hizung.setLastName("Choi");
        //hizung.setId(new BigDecimal(694803));
        hizungClone = (Employee)uow.registerObject(hizung);

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.conformResultsInUnitOfWork();
        result = (Vector)uow.executeQuery(query);

    }

    public void verfiy() {
        if (!result.contains(hizungClone)) {
            throw new TestErrorException("New object could not be recreated correctly.");
        }
    }

    public void reset() {
        uow.release();
        uow = null;
        result = null;
        hizungClone = null;
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
