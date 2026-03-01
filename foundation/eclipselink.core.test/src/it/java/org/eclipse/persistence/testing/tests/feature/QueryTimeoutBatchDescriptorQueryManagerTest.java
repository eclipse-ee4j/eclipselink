/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Bug 214910:  Add query timeout support to batched update queries (Oracle DB 9.0.1+)<br>
 * Test the query timeout feature in batch queries.
 * For data queries , a queryTimeout on the largest DatabaseQuery of the batch will be used.
 * For object queries, a queryTimeout on the largest DescriptorQueryManager (parent) or DatabaseQuery
 * of the batch will be used.
 */
public abstract class QueryTimeoutBatchDescriptorQueryManagerTest extends QueryTimeoutBatchTestCase {

    @Override
    protected  int getParentQueryTimeout() { return 2; }
    @Override
    protected  int getChildQueryTimeout() { return -1; }

    @Override
    protected String getQuerySQLPostfix() {
        return ", SUM(e.address_id) as version from address e, address b, address b, address c, address c, address c, address b";
    }

    /**
     * Test that the queryTimeout passed by the client to the QueryManager is
     * picked up by the BatchWritingMechanism and set on the Statement.
     */
    @Override
    public void test() {
        UnitOfWork uow = null;
        try {
            uow = setupPlatform();
            initializeDatabase(uow);
            // Get new UOW
            uow = getSession().acquireUnitOfWork();
            registerObjects(uow);
            verifyBefore(uow);
               uow.commit();
        } catch (Exception e) {
            /** Throws
             * Internal Exception: java.sql.SQLException: ORA-01013: user requested cancel of current operation
             */
            //e.printStackTrace();
            //System.err.print(e.getMessage());
            if (e instanceof DatabaseException) {
                limitExceeded = true;
                vendorErrorCodeEncountered = ((DatabaseException)e).getDatabaseErrorCode();
            }
            // Release transaction mutex
            //((AbstractSession)uow).rollbackTransaction();
        } finally {
               resetPlatform();
        }
    }

       /**
        * Iterate and register a number of objects in the unitOfWork
        */
       @Override
       protected List<Employee>  registerObjects(UnitOfWork uow) {
           List<Employee> objectListForEditing = new ArrayList<>();
           Address address = null;
           Employee employee = null;
           for (int i = 0; i < getNumberOfInserts(); i++) {
               address = new Address();
               address.setCity("city" + i);
               address.setProvince("province" + i);

               employee = new Employee();
               employee.setFirstName("first" + i);
               employee.setLastName("last" + i);
               //employee.setId(new java.math.BigDecimal(i));
               employee.setAddress(address);

               uow.registerObject(employee);
               // Use the return clone for editing
               objectListForEditing.add(employee);

               // Test framework callback
               // Set the timeout at the descriptor level for each object
               setDescriptorLevelQueryTimeout(uow.getDescriptor(employee).getDescriptorQueryManager());
               setDescriptorLevelQueryTimeout(uow.getDescriptor(address).getDescriptorQueryManager());

               // Test framework callback
               // Set the timeout on each query object (already defined)
               setQueryLevelQueryTimeout(uow, employee);
               setQueryLevelQueryTimeout(uow, address);

               // Add custom insertQuery
               InsertObjectQuery query = new InsertObjectQuery();
               //queryCount++;
               // insert into employee (emp_id, version) SELECT 40003, SUM(e.address_id) as version from address e, address b, address b, address c, address c;
               String sBuffer = getQuerySQLPrefix() + (getCurrentIDSequence() + i) +
                       //sBuffer.append(i);
                       getQuerySQLPostfix();
               query.setSQLString(sBuffer);
               // Override parent
               query.setQueryTimeout(getChildQueryTimeout());
               // We don't set the f_name parameter so Ignore: WARNING: Missing Query parameter for named argument: 1 null will be substituted.
               uow.getDescriptor(employee).getDescriptorQueryManager().setInsertQuery(query);

           }
           return objectListForEditing;
       }

    /**
     * This is a callback from the object loop in registerObjects that allows the test
     * to set a timeout globally on the DescriptorQueryManager
     */
    @Override
    public void setDescriptorLevelQueryTimeout(DescriptorQueryManager queryManager) {
        // Set timeout globally on the root object using the last call appended
        queryManager.setQueryTimeout(getParentQueryTimeout());
    }

    /**
     * This is a callback from the object loop in registerObjects that allows the test
     * to set a timeout on individual queries
     */
    @Override
    public void setQueryLevelQueryTimeout(UnitOfWork uow, Object object) {
    }
}
