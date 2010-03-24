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

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;

// bug 6690525: STACKOVERFLOWERROR UNDER READALLQUERY.EXECUTE
public class BatchReadingStackOverflowTest extends TestCase {
    String firstName = "StackOverflowTest";
    Employee emp_1;
    ForeignReferenceMapping mappingToDisableBatchReadInReset;
    BatchFetchType batchType;
    
    public BatchReadingStackOverflowTest(BatchFetchType batchType) {
        this.batchType = batchType;
        setName(getName() + batchType);
    }
    
    protected void setup() throws Throwable {
        // set readBatch to true on managedEmployees mapping
        mappingToDisableBatchReadInReset = (ForeignReferenceMapping)getSession().getDescriptor(Employee.class).getMappingForAttributeName("managedEmployees");
        if(mappingToDisableBatchReadInReset.shouldUseBatchReading()) {
            // nothing to do - it already uses batch reading
            mappingToDisableBatchReadInReset = null;
        } else {
            mappingToDisableBatchReadInReset.setUsesBatchReading(true);
            mappingToDisableBatchReadInReset.setBatchFetchType(batchType);
        }

        // create objects to be used by the test:
        // emp_1 has two managed employees, each of them has one managed employee.
        emp_1 = new Employee();
        emp_1.firstName = firstName;
        emp_1.lastName = "1";
        emp_1.sex = "male";
        emp_1.managedEmployees = new Vector(2);

        Employee emp_1_1 = new Employee();
        emp_1_1.firstName = firstName;
        emp_1_1.lastName = "1_1";
        emp_1_1.sex = "male";
        emp_1_1.managedEmployees = new Vector(1);
        emp_1.managedEmployees.add(emp_1_1);
        emp_1_1.manager = emp_1;

        Employee emp_1_2 = new Employee();
        emp_1_2.firstName = firstName;
        emp_1_2.lastName = "1_2";
        emp_1_2.sex = "male";
        emp_1_2.managedEmployees = new Vector(1);
        emp_1.managedEmployees.add(emp_1_2);
        emp_1_2.manager = emp_1;

        Employee emp_1_1_1 = new Employee();
        emp_1_1_1.firstName = firstName;
        emp_1_1_1.lastName = "1_1_1";
        emp_1_1_1.sex = "male";
        emp_1_1.managedEmployees.add(emp_1_1_1);
        emp_1_1_1.manager = emp_1_1;

        Employee emp_1_2_1 = new Employee();
        emp_1_2_1.firstName = firstName;
        emp_1_2_1.lastName = "1_2_1";
        emp_1_2_1.sex = "male";
        emp_1_2.managedEmployees.add(emp_1_2_1);
        emp_1_2_1.manager = emp_1_2;

        // Begin transaction here and rollback it in reset.
        ((AbstractSession)getSession()).beginTransaction();
        
        // write the objects into the db, merge them into session's cache.
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(emp_1);
        uow.commit();
        
        // now invalidate all the created objects in the session's cache
        getSession().getIdentityMapAccessor().invalidateObject(emp_1);
        getSession().getIdentityMapAccessor().invalidateObject(emp_1_1);
        getSession().getIdentityMapAccessor().invalidateObject(emp_1_2);
        getSession().getIdentityMapAccessor().invalidateObject(emp_1_1_1);
        getSession().getIdentityMapAccessor().invalidateObject(emp_1_2_1);
        
    }
    
    protected void test() throws Throwable {
        // query to read emp_1
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression exp = builder.get("firstName").equal(firstName).and(builder.get("lastName").equal("1"));
        query.setSelectionCriteria(exp);
        // executing the query used to cause StackOverflow
        Employee empRead = (Employee)getSession().executeQuery(query);
        // the following line is provided just in case you need to put a break point after query execution
        empRead.getManagedEmployees().size();
        query = null;
    }
    
    public void reset() throws Throwable {
        if(((AbstractSession)getSession()).isInTransaction()) {
            ((AbstractSession)getSession()).rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        }

        if(mappingToDisableBatchReadInReset != null) {
            mappingToDisableBatchReadInReset.setUsesBatchReading(false);
            mappingToDisableBatchReadInReset = null;
        }
    }
}
