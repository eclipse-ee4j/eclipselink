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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadAllTest;

/**
 * Test predefined queries.
 *
 */
public class PredefinedInQueryReadAllTest extends ReadAllTest {
    public PredefinedInQueryReadAllTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
        setName("PredefinedInQueryReadAllTest");
    }

    protected void setup() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        org.eclipse.persistence.expressions.ExpressionBuilder builder = new org.eclipse.persistence.expressions.ExpressionBuilder();
        query.setSelectionCriteria(builder.get("salary").in(builder.getParameter("salaries")));
        query.addArgument("salaries");
        setQuery(query);

        getSession().removeQuery("getAllEmployeesIn");
        getSession().addQuery("getAllEmployeesIn", query);

    }

    protected void test() {
        Vector sals = new Vector();
        sals.addElement(new Integer(100));
        sals.addElement(new Integer(56232));
        sals.addElement(new Integer(10000));
        Vector args = new Vector();
        args.addElement(sals);

        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
        // Test execution twice to ensure query is cloned correctly
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
        
        // Also execute the query using a collection type other than vector.
        Collection collection = new HashSet();
        collection.add(new Integer(100));
        collection.add(new Integer(56232));
        collection.add(new Integer(10000));
        args = new Vector();
        args.addElement(collection);

        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
        // Test execution twice to ensure query is cloned correctly
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
    }
}
