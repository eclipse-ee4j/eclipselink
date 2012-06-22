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

import java.util.*;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests fine-grained / descriptor level pessimistic locking.
 * <p>
 * Tests the most complicated case of bug 3584536, and also
 * bug 3524579 PERF: PREPARE ADDS OVERHEAD TO CACHE HIT
 * <p>
 * Query on a project, and then call getTeamLeader(), where address is
 * a joined attribute of employee and pessimistically locked.
 * <p>
 * Even though neither project nor employee are pessimistically locked,
 * A FOR UPDATE OF should be issued triggering teamLeader attribute.
 * <p>
 * Then test that after triggering the indirection that address is
 * pess. locked.
 */
public class PessimisticLockIndirectionJoiningTest extends TestCase {
    public UnitOfWork uow;
    public short lockMode;
    CMPPolicy oldCMPPolicy;
    public int joining;
    ArrayList oldJoinedAttributes;

    public PessimisticLockIndirectionJoiningTest(short lockMode) {
        this.lockMode = lockMode;
        setName(getName() + "(mode=" + lockMode + ")");
        setDescription("This test verifies the pessimistic locking feature works properly when set on the descriptor.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // create a bean-level pess. locking policy on address.
        CMPPolicy cmpPolicy = new CMPPolicy();
        PessimisticLockingPolicy policy = new PessimisticLockingPolicy();
        policy.setLockingMode(this.lockMode);
        cmpPolicy.setPessimisticLockingPolicy(policy);

        // set the pess. locking policy on descriptor, reset selection queries.
        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);
        oldCMPPolicy = addressDescriptor.getCMPPolicy();
        addressDescriptor.setCMPPolicy(cmpPolicy);
        addressDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        employeeDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ObjectLevelReadQuery selectionQuery = ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery());
        selectionQuery.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);

        // make employee.address a joined attribute
        OneToOneMapping addressMapping = (OneToOneMapping)employeeDescriptor.getMappingForAttributeName("address");
        joining = addressMapping.getJoinFetch();
        oldJoinedAttributes = (ArrayList)((ArrayList)selectionQuery.getJoinedAttributeManager().getJoinedMappingExpressions()).clone();
        addressMapping.setJoinFetch(OneToOneMapping.INNER_JOIN);
        employeeDescriptor.reInitializeJoinedAttributes();

        // reset selection query on Project
        ClassDescriptor projectDescriptor = getSession().getDescriptor(Project.class);
        ObjectLevelReadQuery projectSelectionQuery = ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery());
        projectSelectionQuery.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void test() throws Exception {
        checkSelectForUpateSupported();

        if (this.lockMode == ObjectLevelReadQuery.LOCK_NOWAIT) {
            checkNoWaitSupported();     
        }
    
        uow = getSession().acquireUnitOfWork();

        ReadObjectQuery query = new ReadObjectQuery(Project.class);
        Expression expression = query.getExpressionBuilder().get("teamLeader").get("address").get("city").equal("Ottawa");
        query.setSelectionCriteria(expression);

        Project readInProject = (Project)uow.executeQuery(query);

        // Now test that triggering the indirection will issue a for update of query!
        readInProject.getTeamLeader();
        // Test the lock.
        DatabaseSession session2 = null;
        UnitOfWork uow2 = null;
        try {
            org.eclipse.persistence.sessions.Project project = null;
            if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
                project = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getProject();
            } else {
                project = getSession().getProject();
            }
            project = (org.eclipse.persistence.sessions.Project)project.clone();
            project.setLogin((DatabaseLogin)project.getLogin().clone());
            session2 = project.createDatabaseSession();
            session2.setLog(getSession().getLog());
            session2.setLogLevel(getSession().getLogLevel());
            session2.login();
            uow2 = session2.acquireUnitOfWork();

            ReadAllQuery doomedQuery = new ReadAllQuery(Address.class);
            expression = query.getExpressionBuilder().get("city").equal("Ottawa");
            doomedQuery.setSelectionCriteria(expression);

            boolean isLocked = false;
            try {
                List addresses = (List)uow2.executeQuery(doomedQuery);
                addresses.toString();
            } catch (EclipseLinkException exeception) {
                //session2.logMessage(exception.toString());
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
    
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (uow != null) {
            uow.release();
        }
        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);
        addressDescriptor.setCMPPolicy(oldCMPPolicy);
        addressDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        employeeDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery()).dontRefreshIdentityMapResult();
        ObjectLevelReadQuery selectionQuery = ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("address")).getSelectionQuery());
        selectionQuery.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        selectionQuery.getJoinedAttributeManager().setJoinedMappingExpressions_(oldJoinedAttributes);

        // make employee.address a joined attribute
        OneToOneMapping addressMapping = (OneToOneMapping)employeeDescriptor.getMappingForAttributeName("address");
        addressMapping.setJoinFetch(joining);
        employeeDescriptor.reInitializeJoinedAttributes();

        ClassDescriptor projectDescriptor = getSession().getDescriptor(Project.class);
        ObjectLevelReadQuery projectSelectionQuery = ((ObjectLevelReadQuery)((ForeignReferenceMapping)projectDescriptor.getMappingForAttributeName("teamLeader")).getSelectionQuery());
        projectSelectionQuery.setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }
}
