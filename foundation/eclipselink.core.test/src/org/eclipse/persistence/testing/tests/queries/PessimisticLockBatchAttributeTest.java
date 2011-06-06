/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Tests fine-grained / descriptor level pessimistic locking with batch
 * attributes.
 * <p>
 * When a collection of objects are batch-read by one user, these objects
 * should not be accessable to any other user.
 */
public class PessimisticLockBatchAttributeTest extends TestCase {
    public UnitOfWork uow;
    public short lockMode;
    CMPPolicy oldCMPPolicy;

    public PessimisticLockBatchAttributeTest() {
        this.lockMode = ObjectLevelReadQuery.LOCK_NOWAIT;
        setDescription("Verfies that objects pessimistically locked by one user in a batch read are not accessable to others.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        PessimisticLockingPolicy policy = new PessimisticLockingPolicy();
        policy.setLockingMode(this.lockMode);
        CMPPolicy cmpPolicy = new CMPPolicy();
        cmpPolicy.setPessimisticLockingPolicy(policy);

        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("phoneNumbers")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);

        ClassDescriptor phoneDescriptor = getSession().getDescriptor(PhoneNumber.class);
        oldCMPPolicy = phoneDescriptor.getCMPPolicy();
        phoneDescriptor.setCMPPolicy(cmpPolicy);
        phoneDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (uow != null) {
            uow.release();
        }
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("phoneNumbers")).getSelectionQuery()).setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
        ((ObjectLevelReadQuery)((ForeignReferenceMapping)employeeDescriptor.getMappingForAttributeName("phoneNumbers")).getSelectionQuery()).dontRefreshIdentityMapResult();

        ClassDescriptor phoneDescriptor = getSession().getDescriptor(PhoneNumber.class);
        phoneDescriptor.setCMPPolicy(oldCMPPolicy);
        phoneDescriptor.getQueryManager().getReadObjectQuery().setLockMode(ObjectLevelReadQuery.DEFAULT_LOCK_MODE);
    }

    public void test() throws Exception {
        if (!getSession().getPlatform().isOracle() && !getSession().getPlatform().isSQLServer()) {
            throw new TestWarningException("This test only runs on Oracle wears writes do not block reads.");
        }

        uow = getSession().acquireUnitOfWork();

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addBatchReadAttribute(query.getExpressionBuilder().get("phoneNumbers"));

        Vector result = (Vector)uow.executeQuery(query);

        // Now trigger the valueholders...  After this all phoneNumbers should be
        // locked by uow1.
        Vector phones = ((Employee)result.elementAt(0)).getPhoneNumbers();

        // Test the locks.
        // Because this is on a ServerSession the second UOW will have its own
        // ClientSession/exclusive connection.
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        try {
            boolean isLocked = false;
            ReadObjectQuery uow2Query = new ReadObjectQuery(Employee.class);
            Employee employee = (Employee)uow2.executeQuery(uow2Query);

            // assert(employee != null, "It was never meant to be locked.");
            try {
                employee.getPhoneNumbers();
            } catch (EclipseLinkException exception) {
                isLocked = true;
            }
            if (!isLocked) {
                throw new TestErrorException("Triggering an attribute that was locked by another in a batch read should trigger a no_wait exception.");
            }

            // Now release the first UnitOfWork and try again.
            uow.release();
            uow = null;

            try {
                employee.getPhoneNumbers();
            } catch (EclipseLinkException exception) {
                throw new TestErrorException("Now that a batch attribute locked by UOW1 has been released it should be readable now.");
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
