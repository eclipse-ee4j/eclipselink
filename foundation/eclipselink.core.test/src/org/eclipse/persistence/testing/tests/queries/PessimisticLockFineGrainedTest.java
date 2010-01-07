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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Tests fine-grained / descriptor level pessimistic locking.
 * <p>
 * Query on an employee, where the address attribute is joined, and address
 * alone is configured on the descriptor for pessimistic locking.
 * A subsequent attempt to lock the address should fail.
 */
public class PessimisticLockFineGrainedTest extends TestCase {
    public UnitOfWork uow;
    public short lockMode;
    CMPPolicy oldCMPPolicy;

    /**
     * PessimisticLockTest constructor comment.
     */
    public PessimisticLockFineGrainedTest(short lockMode) {
        this.lockMode = lockMode;
        setName(getName() + "(mode=" + lockMode + ")");
        setDescription("This test verifies the pessimistic locking feature works properly when set on the descriptor.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);

        CMPPolicy cmpPolicy = new CMPPolicy();
        PessimisticLockingPolicy policy = new PessimisticLockingPolicy();
        policy.setLockingMode(this.lockMode);

        cmpPolicy.setPessimisticLockingPolicy(policy);
        oldCMPPolicy = addressDescriptor.getCMPPolicy();
        addressDescriptor.setCMPPolicy(cmpPolicy);
        addressDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (uow != null) {
            uow.release();
        }
        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);
        addressDescriptor.setCMPPolicy(oldCMPPolicy);
        addressDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).dontRefreshIdentityMapResult();
    }

    public void test() throws Exception {
        checkSelectForUpateSupported();

        if (lockMode == org.eclipse.persistence.queries.ObjectLevelReadQuery.LOCK_NOWAIT) {
            checkNoWaitSupported();
        }
    
        uow = getSession().acquireUnitOfWork();

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        Expression expression = query.getExpressionBuilder().get("address").get("city").equal("Ottawa");
        query.setSelectionCriteria(expression);
        query.addJoinedAttribute("address");

        Object result = uow.executeQuery(query);
        result.toString();

        // Test the lock.
        DatabaseSession session2 = null;
        UnitOfWork uow2 = null;
        try {
            if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
                session2 = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getProject().createDatabaseSession();
            } else {
                session2 = getSession().getProject().createDatabaseSession();
            }
            session2.setLog(getSession().getLog());
            session2.setLogLevel(getSession().getLogLevel());
            session2.login();
            uow2 = session2.acquireUnitOfWork();
            boolean isLocked = false;
            query = new ReadObjectQuery(Employee.class);
            expression = query.getExpressionBuilder().get("address").get("city").equal("Ottawa");
            query.setSelectionCriteria(expression);
            Employee result2 = (Employee)uow2.executeQuery(query);

            try {
                result2.getAddress();
            } catch (EclipseLinkException exeception) {
                session2.logMessage(exeception.toString());
                isLocked = true;
            }
            if (result2 == null) {
                isLocked = true;
            }
            if (!isLocked) {
                throw new TestErrorException("Select for update does not acquire a lock");
            }
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (uow2 != null) {
                uow2.release();
            }
            if (session2 != null) {
                session2.logout();
            }
        }
    }
}
