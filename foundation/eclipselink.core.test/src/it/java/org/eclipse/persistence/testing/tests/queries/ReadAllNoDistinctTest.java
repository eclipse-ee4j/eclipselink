/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class tests the batch reading feature.
 */
public class ReadAllNoDistinctTest extends ReadAllTest {
    public ReadAllNoDistinctTest() {
        super(Employee.class, 0);
        setName("ReadAllWithNoDistinctTest");
    }

    /**
     * Returns true if duplicates are returned
     */
    protected boolean areDuplicatesReturned() {
        Hashtable employees = new Hashtable();
        for (Enumeration enumtr = ((Vector)objectsFromDatabase).elements();
                 enumtr.hasMoreElements();) {
            Employee employee = (Employee)enumtr.nextElement();
            if (employees.get(employee) != null) {
                return true;
            }
            employees.put(employee, employee);
        }
        return false;
    }

    @Override
    protected void setup() {
        super.setup();
        getQuery().dontUseDistinct();
  //The selection criteria commented out returns no employees. Changed the expression. ET
    //getQuery().setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("number").notEqual(""));
  getQuery().setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("number").notEqual("Empty"));
    }

    @Override
    protected void verify() {
        if (!areDuplicatesReturned()) {
            throw new org.eclipse.persistence.testing.framework.TestException("The DISTINCT was not removed from the SQL");
        }
    }
}
