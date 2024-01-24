/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.mapping;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.mapping.Computer;
import org.eclipse.persistence.testing.models.mapping.Employee;

public class ObjectTypeMappingDefaultNullValues extends TransactionalTestCase {

    /**
     * ObjectTypeMappingDefaultNullValues constructor comment.
     */
    public ObjectTypeMappingDefaultNullValues() {
        super();
    }

    @Override
    protected void test() {
        Computer comp = Computer.example10(new Employee());
        getDatabaseSession().writeObject(comp);

    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    @Override
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Vector computers = getSession().readAllObjects(Computer.class, new ExpressionBuilder().get("isMacintosh").equal(null));
        if (computers.isEmpty()) {
            throw new TestErrorException("Field is empty instead of Null in Object Type Mapping");
        }
    }
}
