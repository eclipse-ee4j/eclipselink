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
package org.eclipse.persistence.testing.tests.queries;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

/**
 * Created for bug 5840824: 1:M - MANY OBJ NOT DELETED UNLESS SPECIFIES PRIVATE OWNED 1:1 BACK RELATIONSHIP
 * Tests deletion of an object that has OneToMany privately owned relation
 * that uses indirection that hasn't been instantiated yet.
 * The indirection should not be triggered - we don't need to read the objects into
 * the cache only to delete them, rather the corresponding rows should be removed from the db.
 */
public class DeleteObjectPrivatelyOwningUntriggeredIndirection extends TestCase {
    BigDecimal id;
  
    public DeleteObjectPrivatelyOwningUntriggeredIndirection() {
    }

    protected void setup()  {
        // create Employee and PhoneNumber, save them in the db
        Employee employee = new Employee();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        employeeClone.setFirstName("A");
        employeeClone.addPhoneNumber(new PhoneNumber("home", "111", "1111111"));
        employeeClone.addPhoneNumber(new PhoneNumber("work", "222", "2222222"));
        uow.commit();
        // cache Employee's id
        id = employee.getId();
        // remove Employee and it's phones from the cache
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test () {
        // read the Employee into the cache - don't trigger phones indirection.
        Employee employee = (Employee)getSession().executeQuery(createReadEmployeeQuery());

        // delete the Employee.
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(employee);
        uow.commit();
    }

    protected void verify()  {
        // verify that the Employee is gone from the cache
        ReadObjectQuery query = createReadEmployeeQuery();
        Employee employee = (Employee)getSession().executeQuery(query);
        if(employee != null) {
            throw new TestErrorException("Deleted Employee is still in the cache");
        }

        // verify that the Employee is gone from the db
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getSession().executeQuery(query);
        if(employee != null) {
            throw new TestErrorException("Deleted Employee is still in the db");
        }
        
        // verify that the Employee's phones are gone from the db
        Vector phones = (Vector)getSession().executeQuery(createReadPhoneNumbersQuery());
        if(!phones.isEmpty()) {
            throw new TestErrorException("Deleted Employee's phones are still in the db");
        }
        
    }

    public void reset() {
        // nothing to do
    }

    protected ReadObjectQuery createReadEmployeeQuery() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadObjectQuery query = new ReadObjectQuery(builder);
        query.setReferenceClass(Employee.class);
        Expression exp = builder.get("id").equal(id);
        query.setSelectionCriteria(exp);
        return query;
    }

    protected ReadAllQuery createReadPhoneNumbersQuery() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(builder);
        query.setReferenceClass(PhoneNumber.class);
        Expression exp = builder.get("owner").get("id").equal(id);
        query.setSelectionCriteria(exp);
        return query;
    }
}
