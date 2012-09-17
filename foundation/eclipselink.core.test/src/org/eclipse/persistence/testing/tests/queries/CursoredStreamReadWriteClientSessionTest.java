/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *     Vikram Bhatia - initial
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.server.*;

/**
 * This test verifies if a transaction is committed while using read & write 
 * connections in same client session with cursored stream used in read. 
 */
public class CursoredStreamReadWriteClientSessionTest extends TestCase {
    protected Server serverSession;
    protected ClientSession clientSession;
    protected int addressId = 0;
    protected Exception caughtException = null; 

    /**
     * CursoredStreamReadWriteClientSessionTest constructor.
     */
    public CursoredStreamReadWriteClientSessionTest() {
        setDescription("Verifies if a transaction is committed while using read & write connections in same client session with cursored stream used in read.");
    }

    public void reset() {
        this.clientSession.release();
        this.serverSession.logout();
    }

    public void setup() {
        org.eclipse.persistence.sessions.Project proj = 
            new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        proj.setDatasourceLogin(getSession().getDatasourceLogin().clone());
        
        // Set initial & min size of connection in pool to ZERO.
        this.serverSession = proj.createServerSession(0, 0, 1);
        this.serverSession.setSessionLog(getSession().getSessionLog());
        this.serverSession.login();
        this.clientSession = this.serverSession.acquireClientSession();
        
        // Fetch a address Id from database.
        Employee emp = (Employee) getSession().readObject(Employee.class);
        this.addressId = emp.getAddress().getId().intValue();
    }

    public void test() {
        clientSession.beginTransaction();
        try {
            // Fetch Address object to update.
            ReadObjectQuery objquery = new ReadObjectQuery();
            objquery.setReferenceClass(Address.class);
            ExpressionBuilder objquerybuilder = new ExpressionBuilder();
            Expression objexpr = objquerybuilder.get("id").equal(this.addressId);
            objquery.setSelectionCriteria(objexpr);
            
            Address address = (Address) clientSession.executeQuery(objquery);
            // This seems to be a very bad test, it is corrupting the database state,
            // Changing the address is not required anyway as this test does no verify,
            // and since it is not using a UnitOfWork, the update will update verything anyway.
            //address.setStreet("Lawrence Street");
            
            UpdateObjectQuery updateQuery = new UpdateObjectQuery();
            updateQuery.setObject(address);
            clientSession.executeQuery(updateQuery);
            
            ReadAllQuery query = new ReadAllQuery();
            query.setReferenceClass(Employee.class);
            query.useCursoredStream(1, 1);
    
            CursoredStream stream = (CursoredStream)clientSession.executeQuery(query);
            Object result = null;
            while (stream.hasNext()) {
                result = stream.next();
            }
            clientSession.commitTransaction();
        } catch(Exception ex) {
            caughtException = ex;
        } finally {
            try {
                clientSession.rollbackTransaction();
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    public void verify() {
        if (caughtException == null) {
            return;
        }
        
        if (caughtException instanceof DatabaseException) {
            throw new TestErrorException("incorrect if the exception is thrown due to a closed connection");
        }
    }
}

