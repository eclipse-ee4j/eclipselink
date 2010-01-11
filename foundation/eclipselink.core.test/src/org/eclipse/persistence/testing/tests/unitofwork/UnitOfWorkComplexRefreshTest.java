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

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.remote.RemoteModel;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test that the refresh in a unit of work does not needlessly commit changes.
 */
public class UnitOfWorkComplexRefreshTest extends AutoVerifyTestCase {
    protected Employee dbEmployee1;
    protected Employee dbEmployee2;
    protected UnitOfWork uow1;
    protected UnitOfWork uow2;
    protected Writer oldLog;
    protected int oldLogLevel;
    protected StringWriter tempWriter;
    // On some platforms (Sybase) if conn1 updates a row but hasn't yet committed transaction then
    // reading the row through conn2 may hang.
    // To avoid this problem the listener would decrement transaction isolation level,
    // then reading through conn2 no longer hangs, however may result (results on Sybase)
    // in reading of uncommitted data.
    SessionEventListener listener;

    public UnitOfWorkComplexRefreshTest() {
        setDescription("Test that a refreshed object in unit of work does not generate sql on commit.");
    }

    public void setup() {
        if (getSession().isClientSession()) {
            checkTransactionIsolation();
        }
        
        getAbstractSession().beginTransaction();
        uow1 = getSession().acquireUnitOfWork();
        uow2 = getSession().acquireUnitOfWork();

        Expression exp = new ExpressionBuilder().get("firstName").equal("Charles");
        dbEmployee1 = (Employee)uow1.readObject(Employee.class, exp);
        dbEmployee2 = (Employee)uow2.readObject(Employee.class, exp);
        dbEmployee1.getAddress().setCity("Bobstown");
    }

    public void reset() {
        if(getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if(listener != null) {
            getAbstractSession().getParent().getEventManager().removeListener(listener);
            listener = null;
        }
    }

    public void switchLog(Session aSession) {
        if (aSession instanceof org.eclipse.persistence.internal.sessions.remote.RemoteUnitOfWork) {
            aSession = RemoteModel.getServerSession();
        }
        oldLog = aSession.getLog();
        oldLogLevel = aSession.getLogLevel();
        aSession.setLogLevel(SessionLog.FINE);
        tempWriter = new StringWriter();
        aSession.setLog(tempWriter);
    }

    public void switchLoggingBack(Session aSession) {
        if (aSession.isRemoteUnitOfWork()) {
            aSession = RemoteModel.getServerSession();
        }
        aSession.setLogLevel(oldLogLevel);
        aSession.setLog(oldLog);
    }

    public void test() {
        dbEmployee2.getAddress().setCity("Yousersville");
        uow2.commit();

        uow1.refreshObject(dbEmployee1);
        switchLog(uow1);
        uow1.commit();
        switchLoggingBack(uow1);
    }

    public void verify() {
        if (dbEmployee1.getAddress().getCity().equals("Bobstown")) {
            // The address was not properly refreshed.
            throw new TestErrorException("The object in the unit of work was properly refreshed.");
        }

        if (tempWriter.toString().indexOf("UPDATE ADDRESS SET CITY = 'Yousersville'") != -1) {
            throw new TestErrorException("The second commit updated the database, but should not have, '" + 
                                         tempWriter.toString() + "'");
        }
    }
}
