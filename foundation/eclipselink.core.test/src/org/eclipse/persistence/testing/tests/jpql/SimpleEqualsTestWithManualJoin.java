/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

//Test id comparison without 1:1, that must force a join
public class SimpleEqualsTestWithManualJoin extends JPQLTestCase {
    public void setup() {
        setReferenceClass(Employee.class);

        //add a mapping for addressId so we can use it in from "emp"
        //        DirectToFieldMapping addressIdMapping = new DirectToFieldMapping();
        //        addressIdMapping.setAttributeName("addressId");
        //        addressIdMapping.setFieldName("EMPLOYEE.ADDR_ID");
        //        addressIdMapping.setGetMethodName("getAddressId");
        //        addressIdMapping.setSetMethodName("setAddressId");
        //        addressIdMapping.setIsReadOnly(true);
        //        getSession().getDescriptor(Employee.class).addMapping(addressIdMapping);
        //        addressIdMapping.initialize(getSession());
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder addressBuilder = new ExpressionBuilder(Address.class);
        Expression whereClause = builder.get("addressId").equal(addressBuilder.get("id"));
        Vector employees = getSession().readAllObjects(Employee.class, whereClause);

        setOriginalOject(employees);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(e) FROM Employee e, Address a " + "WHERE e.addressId = a.id";

        setEjbqlString(ejbqlString);
        super.setup();
    }

    public void reset() {
        //        getSession().getDescriptor(Employee.class).removeMappingForAttributeName("addressId");
    }
}
