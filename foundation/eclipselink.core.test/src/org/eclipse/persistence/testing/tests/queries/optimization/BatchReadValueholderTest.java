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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Bug 4230655 - Ensure instantiated valueholders are not reset when a batch query runs.
 */
public class BatchReadValueholderTest extends TestCase {
    protected Vector employees = null;
    BatchFetchType batchType;

    public BatchReadValueholderTest(BatchFetchType batchType) {
        setDescription("Ensure instantiated valueholders are not reset when a batch query runs.");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setBatchFetchType(batchType);
        query.addBatchReadAttribute("manager");
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Jim-bob");
        query.setSelectionCriteria(exp);
        Vector emps = (Vector)getSession().executeQuery(query);
        Iterator i = emps.iterator();
        while (i.hasNext()) {
            Employee e = (Employee)i.next();
            Employee m = (Employee)e.getManager();
            if (m != null) {
                m.hashCode();
            }
        }
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addBatchReadAttribute("manager");
        employees = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        Iterator i = employees.iterator();
        while (i.hasNext()) {
            Employee emp = (Employee)i.next();
            if (emp.getFirstName().equals("Jim-bob") && emp.getLastName().equals("Jefferson") && !emp.manager.isInstantiated()) {
                throw new TestErrorException("A batch read query changed an instantiated valueholder to uninstantiated.");
            } else if (emp.getFirstName().equals("John") && emp.getLastName().equals("Way") && emp.manager.isInstantiated()) {
                throw new TestErrorException("A valueholder was incorrectly instantiated by a batch read query.");
            }
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
