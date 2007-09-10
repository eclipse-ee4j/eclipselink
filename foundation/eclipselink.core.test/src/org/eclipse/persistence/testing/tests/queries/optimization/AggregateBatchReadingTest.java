/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.*;

public class AggregateBatchReadingTest extends TestCase {
    public Vector result;

    public AggregateBatchReadingTest() {
        setDescription("Tests batch reading nesting across an aggregate");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        // changed attribute name from "addressDescription" to "address" to reproduce bug 3566341,
        // which requires the same name for attribute mapped as an aggregate and an attribute
        // on the aggregate mapped 1 to 1.
        query.addBatchReadAttribute(query.getExpressionBuilder().get("address").get("address"));
        result = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        for (Enumeration employeesEnum = result.elements(); employeesEnum.hasMoreElements(); ) {
            ((Employee)employeesEnum.nextElement()).getAddressDescription().getAddress().getValue();
        }
    }
}
