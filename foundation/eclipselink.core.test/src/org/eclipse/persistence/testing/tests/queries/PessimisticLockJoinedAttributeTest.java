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
package org.eclipse.persistence.testing.tests.queries;

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
 * Tests fine-grained / descriptor level pessimistic locking with joined
 * attributes.
 * <p>
 * Specifically tests the unusual cases of bug 3422202, and makes sure the
 * joined attributes are properly recorded with the correct session.
 * <p>
 * This test must be run using the ServerSessionTestAdaptor, so that each
 * UnitOfWork is on a separate UnitOfWork, each having their own transaction
 * but still sharing the global cache.
 * <p>
 * Test cases:
 * <ul>
 * <li>Query on a project, where team leader and its address are joined.  Query
 * for both the address and the employee: and check the SQL to see if had
 * a cache hit (or checkCacheOnly).
 * <li>Query on a project, where team leader is joined.  Then in a separate UnitOfWork
 * query on the same project where team leader is not joined, and then attempt
 * project.getTeamLeader().  This should fail.
 * <li>Query on a project, where team leader is not joined.  Then in a separate
 * UnitOfWork query on the project where team leader is joined.  Then try to
 * get the teamleader on the first UnitOfWork.  This should fail.
 * <li>Query on a project, where team leader is joined, and rollback.  Change
 * the cache copy and then have another read the project where team leader is
 * joined.  The query and the refresh should succeed.
 * </ul>
 */
public class PessimisticLockJoinedAttributeTest extends TestCase {
    public UnitOfWork uow;
    public short lockMode;
    CMPPolicy oldCMPPolicy;

    /**
     * PessimisticLockInheritanceTest constructor comment.
     */
    public PessimisticLockJoinedAttributeTest() {
        this.lockMode = ObjectLevelReadQuery.LOCK_NOWAIT;
        setDescription("For bug 3422202 verifies the pessimistic locking feature works properly when set on the descriptor and joined attributes are involved.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        PessimisticLockingPolicy policy = new PessimisticLockingPolicy();
        policy.setLockingMode(this.lockMode);
        CMPPolicy cmpPolicy = new CMPPolicy();
        cmpPolicy.setPessimisticLockingPolicy(policy);

        ClassDescriptor projectDescriptor = getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);

        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        oldCMPPolicy = employeeDescriptor.getCMPPolicy();
        employeeDescriptor.setCMPPolicy(cmpPolicy);
        employeeDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);

        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);
        addressDescriptor.setCMPPolicy(cmpPolicy);
        addressDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (uow != null) {
            uow.release();
        }
        ClassDescriptor projectDescriptor = getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery()).dontRefreshIdentityMapResult();
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        employeeDescriptor.setCMPPolicy(oldCMPPolicy);
        employeeDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);

        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).dontRefreshIdentityMapResult();

        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);
        addressDescriptor.setCMPPolicy(oldCMPPolicy);
        addressDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void test() throws Exception {
        if (!getSession().getPlatform().isOracle() && !getSession().getPlatform().isSQLServer()) {
            throw new TestWarningException("This test only runs on Oracle wears writes do not block reads.");
        }

        uow = getSession().acquireUnitOfWork();

        ReadObjectQuery query = new ReadObjectQuery(LargeProject.class);

        // Only Charles Chanley and John Way are team leaders of projects they are also working on.
        Expression expression = query.getExpressionBuilder().get("teamLeader").get("firstName").equal("Charles");
        query.setSelectionCriteria(expression);
        query.addJoinedAttribute(query.getExpressionBuilder().get("teamLeader"));
        query.addJoinedAttribute(query.getExpressionBuilder().get("teamLeader").get("address"));

        Object result = uow.executeQuery(query);

        // Now trigger the valueholders...  These clones should be registered in
        // the uow and marked as already locked.
        // Check the SQL so that none gets issued.
        Employee charles = (Employee)((LargeProject)result).getTeamLeader();
        Address address = charles.getAddress();

        ReadObjectQuery cacheQuery = null;
        Object cachedObject = null;

        // There is only one address in the cache, and it will only be returned
        // if it was tracked as being locked.
        cacheQuery = new ReadObjectQuery(Address.class);
        cacheQuery.checkCacheThenDatabase();
        cachedObject = uow.executeQuery(cacheQuery);
        if (address != cachedObject) {
            throw new TestErrorException("Did not get a cache hit after pessimistically locking a nested joined attribute.");
        }
        cacheQuery = new ReadObjectQuery(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        cacheQuery.checkCacheThenDatabase();
        cachedObject = uow.executeQuery(cacheQuery);
        if (charles != cachedObject) {
            throw new TestErrorException("Did not get a cache hit after pessimisticly locking a joined attribute.");
        }

        // Test the lock.
        // Because this is on a ServerSession the second UOW will have its own
        // ClientSession/exclusive connection.
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        try {
            boolean isLocked = false;
            query = new ReadObjectQuery(LargeProject.class);
            expression = query.getExpressionBuilder().get("teamLeader").get("firstName").equal("Charles");
            query.setSelectionCriteria(expression);
            LargeProject result2 = (LargeProject)uow2.executeQuery(query);

            // assert(result2 != null, "It was never meant to be locked.");
            try {
                result2.getTeamLeader();
            } catch (EclipseLinkException exception) {
                isLocked = true;
            }
            if (!isLocked) {
                throw new TestErrorException("Triggering an attribute that was locked by another in a joined read should trigger a no_wait exception.");
            }

            // Now release the first UnitOfWork and try again.
            Employee originalCharles = (Employee)uow.getOriginalVersionOfObject(charles);
            uow.release();
            uow = null;

            // Now change the session copy, this is what would happen if the
            // object was actually committed to the database.  Want to test that
            // the object is refreshed when locked.
            originalCharles.setSalary(0);
            try {
                charles = (Employee)result2.getTeamLeader();
                address = charles.getAddress();
            } catch (EclipseLinkException exception) {
                throw new TestErrorException("Now that a joined attribute locked by UOW1 has been released it should be readable now.");
            }
            if (charles.getSalary() == 0) {
                throw new TestErrorException("When a joined attribute is locked by another after it is released, should get refreshed.");
            }
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (uow2 != null) {
                uow2.release();
            }
        }
    }
}
