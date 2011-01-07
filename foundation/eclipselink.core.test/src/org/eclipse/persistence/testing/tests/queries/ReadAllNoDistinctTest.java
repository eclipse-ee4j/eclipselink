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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    protected void setup() {
        super.setup();
        getQuery().dontUseDistinct();
  //The selection criteria commented out returns no employees. Changed the expression. ET
	//getQuery().setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("number").notEqual(""));
  getQuery().setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("number").notEqual("Empty"));
    }

    protected void verify() {
        if (!areDuplicatesReturned()) {
            throw new org.eclipse.persistence.testing.framework.TestException("The DISTINCT was not removed from the SQL");
        }
    }
}
