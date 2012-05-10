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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test read-object queries with no where clause an 1-m joining.
 */
public class ReadAnyObjectJoinPhoneTest extends AutoVerifyTestCase {
    public ReadAnyObjectJoinPhoneTest() {
        setDescription("Test read-object queries with no where clause an 1-m joining.");
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("phoneNumbers"));
        Employee employee = (Employee)getSession().executeQuery(query);

        if (!employee.phoneNumbers.isInstantiated()) {
            throw new TestErrorException("Employee phones not populated from join.");
        }

        // Test that employee has correct number of phones.
        if (employee.getPhoneNumbers().size() > 5) {
            throw new TestErrorException("Employee phones not filtered.");
        }
    }
}
