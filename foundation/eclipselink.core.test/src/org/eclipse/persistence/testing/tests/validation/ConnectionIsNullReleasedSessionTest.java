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
 *     CDelahunt - bug 273338 NullPointerException possible in DatabaseAccessor  
 ******************************************************************************/    

package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * This test validates that the appropriate databaseAccessorConnectionIsNull is thrown
 * when afterCompletion releases the sessions while a separate thread is executing a query
 */
public class ConnectionIsNullReleasedSessionTest extends ExceptionTest implements QueryRedirector {
    UnitOfWork uow;
    ClientSession cl;
    public Object invokeQuery(DatabaseQuery query, Record record, Session session) {
        uow.release();
        //This will null out the write accessorr
        cl.release();
        //session should be the uow just released, but still be in a transaction
        return session.executeQuery(query);
    }
    protected void setup() {
        expectedException = org.eclipse.persistence.exceptions.DatabaseException.databaseAccessorConnectionIsNull(null, null);
    }

    public void test() {
        org.eclipse.persistence.sessions.DatabaseSession session = this.getDatabaseSession();
        org.eclipse.persistence.sessions.server.Server newsession = session.getProject().createServerSession();
        newsession.login();
        cl = newsession.acquireClientSession();
        uow = cl.acquireUnitOfWork();
        try {
            //start a transaction so that reads go through the ClientSession's writeAccessor
            cl.beginTransaction();
            ReadAllQuery query = new ReadAllQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
            query.setRedirector(this);
            uow.executeQuery(query);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        } finally {
            if (cl!=null && cl.isInTransaction()){
                cl.rollbackTransaction();
                cl=null;
            }
            if (newsession!=null && newsession.isConnected()) {
                newsession.logout();
            }
        }
    }

}
