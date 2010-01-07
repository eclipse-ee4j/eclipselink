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

import java.util.*;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests fine-grained / descriptor level pessimistic locking in an
 * inheritance hierarchy.
 * <p>
 * Query on a large project, where the team leader attribute is joined, and
 * project alone is configured on the descriptor for pessimistic locking.
 * A subsequent attempt to read the project should fail, but not to lock the
 * employee.
 */
public class PessimisticLockInheritanceTest extends TestCase {
    public UnitOfWork uow;
    public short lockMode;
    CMPPolicy oldCMPPolicy;

    /**
     * PessimisticLockInheritanceTest constructor comment.
     */
    public PessimisticLockInheritanceTest(short lockMode) {
        this.lockMode = lockMode;
        setName(getName() + "(mode=" + lockMode + ")");
        setDescription("This test verifies the pessimistic locking feature works properly when set on the descriptor and inheritance is involved.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ClassDescriptor projectDescriptor = getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        ClassDescriptor largeProjectDescriptor = getSession().getDescriptor(LargeProject.class);
        ClassDescriptor smallProjectDescriptor = getSession().getDescriptor(SmallProject.class);

        CMPPolicy cmpPolicy = new CMPPolicy();
        PessimisticLockingPolicy policy = new PessimisticLockingPolicy();
        policy.setLockingMode(this.lockMode);

        cmpPolicy.setPessimisticLockingPolicy(policy);
        oldCMPPolicy = projectDescriptor.getCMPPolicy();
        projectDescriptor.setCMPPolicy(cmpPolicy);
        projectDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("projects")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        largeProjectDescriptor.setCMPPolicy(cmpPolicy);
        largeProjectDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        smallProjectDescriptor.setCMPPolicy(cmpPolicy);
        smallProjectDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (uow != null) {
            uow.release();
        }
        ClassDescriptor projectDescriptor = getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        ClassDescriptor largeProjectDescriptor = getSession().getDescriptor(LargeProject.class);
        ClassDescriptor smallProjectDescriptor = getSession().getDescriptor(SmallProject.class);
        projectDescriptor.setCMPPolicy(oldCMPPolicy);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery()).dontRefreshIdentityMapResult();
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("projects")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("projects")).getSelectionQuery()).dontRefreshIdentityMapResult();
        largeProjectDescriptor.setCMPPolicy(oldCMPPolicy);
        smallProjectDescriptor.setCMPPolicy(oldCMPPolicy);
    }

    public void test() throws Exception {
        checkSelectForUpateSupported();

        if (this.lockMode == ObjectLevelReadQuery.LOCK_NOWAIT) {
            checkNoWaitSupported();
        }
    
        uow = getSession().acquireUnitOfWork();

        ReadObjectQuery query = new ReadObjectQuery(LargeProject.class);

        // Only Charles Chanley and John Way are team leaders of projects they are also working on.
        Expression expression = query.getExpressionBuilder().get("teamLeader").get("firstName").equal("Charles");
        query.setSelectionCriteria(expression);
        query.addJoinedAttribute("teamLeader");

        Object result = uow.executeQuery(query);

        // Now trigger the valueholder...
        ((LargeProject)result).getTeamLeader();

        ReadObjectQuery cacheQuery = new ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        cacheQuery.checkCacheOnly();
        Object cachedObject = uow.executeQuery(cacheQuery);
        if (result != cachedObject) {
            throw new TestErrorException("Did not get a cache hit when using pessimistic locking.");
        }
        cacheQuery = new ReadObjectQuery(Employee.class);
        cacheQuery.checkCacheOnly();
        cachedObject = uow.executeQuery(cacheQuery);
        if (((LargeProject)result).getTeamLeader() != cachedObject) {
            throw new TestErrorException("Did not get a cache hit after pessimisticly locking a joined attribute.");
        }
        Vector projects = ((Employee)cachedObject).getProjects();

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
            expression = query.getExpressionBuilder().get("firstName").equal("Charles");
            query.setSelectionCriteria(expression);
            Employee result2 = (Employee)uow2.executeQuery(query);
            if (result2 == null) {
                throw new TestErrorException("A joined attribute was mistakenly locked pessimistically.");
            }
            projects = null;
            try {
                projects = result2.getProjects();
            } catch (EclipseLinkException exeception) {
                session2.logMessage(exeception.toString());
                isLocked = true;
            }
            if (projects == null) {
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
