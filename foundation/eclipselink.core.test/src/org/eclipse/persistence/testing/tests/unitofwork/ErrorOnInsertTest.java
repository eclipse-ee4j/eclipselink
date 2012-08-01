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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import java.sql.Date;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DynamicSQLBatchWritingMechanism;
import org.eclipse.persistence.internal.databaseaccess.ParameterizedSQLBatchWritingMechanism;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;


/**
 * <b>Purpose:</b>Tests that new cache keys are removed from the cache on error.
 * It is very specific to a WB usecase.
 */
public class ErrorOnInsertTest extends AutoVerifyTestCase {
    public Employee mary;

    public ErrorOnInsertTest() {
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    protected void test() {
        if (getSession().isClientSession() || getSession().isDistributedSession()) {
            throw new TestWarningException("Unable to trigger error in ClientSession or Remote UnitOfWork");
        }
        mary = new Employee();
        mary.setFirstName("Mary");
        mary.setLastName("Magdalene");
        mary.setId(new BigDecimal(100002929));
        mary.setPeriod(new EmploymentPeriod(new Date(3), new Date(3455)));

        UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        DatabaseAccessor accessor = (DatabaseAccessor)uow.getParent().getAccessor();
        uow.getParent().setAccessor(new DummyAccessor(accessor));
        Employee maryClone = (Employee)uow.registerObject(mary);
        UnitOfWorkChangeSet uowcs = (UnitOfWorkChangeSet)uow.getCurrentChanges();
        try {
            uow.commitAndResumeWithPreBuiltChangeSet(uowcs);
        } catch (Exception ex) {
        }
        uow.revertAndResume();
        uow.getParent().setAccessor(accessor);

        try {
            uow.commitAndResumeWithPreBuiltChangeSet(uowcs);
        } catch (QueryException ex) {
            if (ex.getErrorCode() == 6004) {
                throw new TestErrorException("New Object was not inserted because cachekey was not removed");
            } else {
                throw ex;
            }
        }
        uow.commit();
    }

    public void reset() {
        try {
            getDatabaseSession().deleteObject(this.mary);
        } catch (Exception ex) {
            //just ignore, if test fails an optLock exception will be thrown
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected class DummyAccessor extends DatabaseAccessor {
        public DummyAccessor(DatabaseAccessor accessor) {
            this.datasourceConnection = accessor.getDatasourceConnection();
            this.login = accessor.getLogin();
            this.callCount = accessor.getCallCount();
            this.isInTransaction = accessor.isInTransaction();
            this.isConnected = accessor.isConnected();
            this.platform = accessor.getPlatform();
            this.activeBatchWritingMechanism = accessor.getActiveBatchWritingMechanism();
            this.dynamicSQLMechanism = new DynamicSQLBatchWritingMechanism(this);
            this.parameterizedMechanism = new ParameterizedSQLBatchWritingMechanism(this);
            this.lobWriter = accessor.getLOBWriter();
        }

        public void basicCommitTransaction(AbstractSession session) throws DatabaseException {
            throw DatabaseException.databaseAccessorNotConnected();
        }
    }
}
