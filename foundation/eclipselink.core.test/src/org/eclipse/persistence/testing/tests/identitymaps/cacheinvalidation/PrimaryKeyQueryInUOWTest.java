/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.sql.*;

import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.framework.*;

//Bug#4613774  primary key queries in general should not ignore invalidation when executed in a unit of work
public class PrimaryKeyQueryInUOWTest extends CacheExpiryTest {
    String originalName;
    String newName;

    public PrimaryKeyQueryInUOWTest() {
        setDescription("Test Cache Expiry a read Object Query.");
    }

    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000));
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadObjectQuery rq = new ReadObjectQuery(Employee.class);
        rq.conformResultsInUnitOfWork();
        Expression exp = new ExpressionBuilder().get("firstName").equal("Bob");
        rq.setSelectionCriteria(exp);
        Employee employee = (Employee)uow.executeQuery(rq);
        uow.commit();

        try {
            Connection con = ((AbstractSession)getSession()).getAccessor().getConnection();
            Statement sqlStmt = con.createStatement();
            String str =
                "UPDATE EMPLOYEE SET L_NAME = 'Ray', VERSION = 2 WHERE ((EMP_ID = " + employee.getId() + ") AND (VERSION = 1))";
            sqlStmt.executeUpdate(str);
            sqlStmt.close();
            //            con.close();
        } catch (Exception e) {

        }

        try {
            Thread.sleep(7000);
        } catch (Exception ex) {

        }

        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        rq = new ReadObjectQuery(Employee.class);
        rq.conformResultsInUnitOfWork();
        rq.setSelectionObject(employee);
        Employee employee2 = (Employee)uow2.executeQuery(rq);
        originalName = employee2.getLastName();
        uow2.commit();

        try {
            Thread.sleep(8000);
        } catch (Exception ex) {

        }

        UnitOfWork uow3 = getSession().acquireUnitOfWork();
        rq = new ReadObjectQuery(Employee.class);
        rq.conformResultsInUnitOfWork();
        rq.setSelectionObject(employee);
        employee2 = (Employee)uow3.executeQuery(rq);
        newName = employee2.getLastName();
        uow3.commit();
    }

    public void verify() {
        if (!originalName.equals("Smith")) {
            throw new TestErrorException("The original name before expiry should be Smith, but " + originalName +
                                         " was returned");
        } else if (!newName.equals("Ray")) {
            throw new TestErrorException("The new name after expiry should be Ray, but " + newName + " was returned");
        }
    }

}
