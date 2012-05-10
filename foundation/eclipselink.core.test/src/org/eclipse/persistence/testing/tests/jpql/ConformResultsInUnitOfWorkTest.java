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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import java.math.BigDecimal;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Verify that ReadObjectQuery with conformResultsInUnitOfWork(), and using EJBQL
 * works properly.
 * Use Case: Create an object (brand new) in the unit of work, and try to read
 *           it using conformResultsInUnitOfWork();
 */
public class ConformResultsInUnitOfWorkTest extends JPQLTestCase {
    public void test() throws Exception {
        ReadObjectQuery readObjectQuery = new ReadObjectQuery();

        readObjectQuery.setReferenceClass(Employee.class);
        readObjectQuery.setEJBQLString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = ?1");
        readObjectQuery.conformResultsInUnitOfWork();
        readObjectQuery.addArgument("1", BigDecimal.class);

        /* test against DatabaseSession, then against ClientSession */

        //DatabaseSession is the default
        UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
        Employee newEmployee = new Employee();
        newEmployee.setId(new BigDecimal(9000));
        unitOfWork.registerObject(newEmployee);

        Vector testV = new Vector();
        testV.addElement(new BigDecimal(9000));

        Employee result = (Employee)unitOfWork.executeQuery(readObjectQuery, testV);
        if (result == null) {
            throw new TestErrorException("DatabaseSession test: employee with id 9000 expected--returned null");
        }

        //ServerSession next
        Server serverSession = getSession().getProject().createServerSession();
        serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();
        unitOfWork = serverSession.acquireClientSession().acquireUnitOfWork();
        newEmployee = new Employee();
        newEmployee.setId(new BigDecimal(9000));
        unitOfWork.registerObject(newEmployee);

        testV = new Vector();
        testV.addElement(new BigDecimal(9000));

        result = (Employee)unitOfWork.executeQuery(readObjectQuery, testV);
        serverSession.logout();

        if (result == null) {
            throw new TestErrorException("ClientSession test: employee with id 9000 expected--returned null");
        }
    }
}
