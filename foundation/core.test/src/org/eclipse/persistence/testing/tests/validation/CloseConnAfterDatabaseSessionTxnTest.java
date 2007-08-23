/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

/**
 * for bug 6065882, Verify databaseSession connection should close after txn was finished(either commit or rollback txn)
 * regardless the txn controlled by ExternalTransactionController
 */
public class CloseConnAfterDatabaseSessionTxnTest extends AutoVerifyTestCase {
    protected DatabaseSession session = null;
    protected boolean hasExternalTransactionController = false;
    
    public CloseConnAfterDatabaseSessionTxnTest(boolean hasExternalTransactionController){
        if(hasExternalTransactionController){
           setDescription("Ensure the connection closed properly once query finished - DatabaseSession has ExternalTransactionController");
        } else {
            setDescription("Ensure the connection closed properly once query finished - DatabaseSession has no ExternalTransactionController");
        }
        this.hasExternalTransactionController = hasExternalTransactionController;
    }
    
    public static class DummyExternalTransactionController extends org.eclipse.persistence.transaction.AbstractTransactionController {
        public boolean isRolledBack_impl(Object status){return false;}
        protected void registerSynchronization_impl(org.eclipse.persistence.transaction.AbstractSynchronizationListener listener, Object txn) throws Exception{}
        protected Object getTransaction_impl() throws Exception {return null;}
        protected Object getTransactionKey_impl(Object transaction) throws Exception {return null;}
        protected Object getTransactionStatus_impl() throws Exception {return null;}
        protected void beginTransaction_impl() throws Exception{}
        protected void commitTransaction_impl() throws Exception{}
        protected void rollbackTransaction_impl() throws Exception{}
        protected void markTransactionForRollback_impl() throws Exception{}
        protected boolean canBeginTransaction_impl(Object status){return false;}
        protected boolean canCommitTransaction_impl(Object status){return false;}
        protected boolean canRollbackTransaction_impl(Object status){return false;}
        protected boolean canIssueSQLToDatabase_impl(Object status){return false;}
        protected boolean canMergeUnitOfWork_impl(Object status){return false;}
        protected String statusToString_impl(Object status){return "";}
    }
    
    public void setup() {
        org.eclipse.persistence.sessions.Project project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        DatasourceLogin clonedLogin = (DatasourceLogin)((org.eclipse.persistence.sessions.DatabaseSession)getSession()).getProject().getDatasourceLogin().clone();
        project.setLogin(clonedLogin);
        clonedLogin.useExternalConnectionPooling();
        if(hasExternalTransactionController){
            clonedLogin.useExternalTransactionController();
         }
        session=project.createDatabaseSession();
        if(hasExternalTransactionController){
            session.setExternalTransactionController(new DummyExternalTransactionController());
         }
        session.login();
    }

    public void test() {
        EmployeePopulator system = new EmployeePopulator();
        Object employee = system.basicEmployeeExample1(); 
        session.insertObject(employee);
        session.deleteObject(employee);
    }

    public void verify() {
        if(((AbstractSession)session).getAccessor().getDatasourceConnection()!=null){
            throw new TestErrorException("The connection expected to close which still open after a txn was finished.");
        }
    }

    public void reset() {
        session.logout();
    }
}