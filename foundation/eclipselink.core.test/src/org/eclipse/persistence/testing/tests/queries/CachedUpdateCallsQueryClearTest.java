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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test to ensure that DescriptorQueryManager's cached update calls do not have their
 * 'query' reference set. This is to prevent a memory leak from occurring.
 * @author dminsky
 */
public class CachedUpdateCallsQueryClearTest extends TestCase {

    public CachedUpdateCallsQueryClearTest() {
        setDescription("Test that the DescriptorQueryManager's cached update calls query attribute is de-referenced");
    }
    
    public void setup() {
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getDatabaseSession().beginTransaction();
    }
    
    public void test() {
        UnitOfWork uow = getDatabaseSession().acquireUnitOfWork();
        Employee employee = (Employee) uow.readObject(
            Employee.class, 
            new ExpressionBuilder().get("lastName").equal("Smitty"));
        assertNotNull(employee);
        employee.setLastName("Archibald");
        uow.commit();
    }
    
    public void verify() {
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        DescriptorQueryManager descriptorQueryManager = descriptor.getDescriptorQueryManager();
        // in the update transaction in test(), the lastName and the version fields are updated
        Vector fields = new Vector(2);
        fields.add(descriptor.getMappingForAttributeName("lastName").getField());
        fields.add(descriptor.getOptimisticLockingPolicy().getWriteLockField());
        
        Vector cachedUpdateCalls = descriptorQueryManager.getCachedUpdateCalls(fields);
        assertNotNull(cachedUpdateCalls);
        assertFalse(cachedUpdateCalls.isEmpty());
        
        Iterator<DatasourceCall> iterator = cachedUpdateCalls.iterator();

        while (iterator.hasNext()) {
            DatasourceCall call = iterator.next();
            // calls should not cache a query
            if (call.getQuery() != null) {
                throw new TestErrorException("DatasourceCall's query is not null: " + call);
            }
        }
    }
    
    public void reset() {
        getDatabaseSession().rollbackTransaction();
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }    
    
}
