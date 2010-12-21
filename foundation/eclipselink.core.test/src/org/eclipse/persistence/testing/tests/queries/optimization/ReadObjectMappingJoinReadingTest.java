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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This class tests mapping level 1-1 joins with the read-object query.
 * The join was being ignored for read-object queries in the unit of work.
 * CR#3823735
 */
public class ReadObjectMappingJoinReadingTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    Employee employee;
    int oldJoinFetch;

    protected void setup() {
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        oldJoinFetch = ((OneToOneMapping)descriptor.getMappingForAttributeName("address")).getJoinFetch();
        ((OneToOneMapping)descriptor.getMappingForAttributeName("address")).useInnerJoinFetch();
        descriptor.reInitializeJoinedAttributes();
        // Must reset the descriptor's read-object query.
        ReadObjectQuery readObjectQuery = new ReadObjectQuery();
        readObjectQuery.setSelectionCriteria(descriptor.getObjectBuilder().getPrimaryKeyExpression());
        descriptor.getQueryManager().setReadObjectQuery(readObjectQuery);
        employee = (Employee)getSession().readObject(Employee.class);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Expression expression = new ExpressionBuilder().get("id").equal(employee.getId());
        employee = (Employee)uow.readObject(Employee.class, expression);
    }

    protected void verify() {
        Employee original = (Employee)getSession().readObject(employee);
        if (!original.address.isInstantiated()) {
            throw new TestErrorException("Employee's address was not joined.");
        }
        employee.getAddress().getCity();
    }

    public void reset() {
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        ((OneToOneMapping)descriptor.getMappingForAttributeName("address")).setJoinFetch(oldJoinFetch);
        descriptor.reInitializeJoinedAttributes();
        // Must reset the descriptor's read-object query.
        ReadObjectQuery readObjectQuery = new ReadObjectQuery();
        readObjectQuery.setSelectionCriteria(descriptor.getObjectBuilder().getPrimaryKeyExpression());
        descriptor.getQueryManager().setReadObjectQuery(readObjectQuery);
    }
}
