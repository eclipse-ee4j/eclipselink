/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    protected void test() {
        Computer comp = new Computer().example10(new Employee());
        getDatabaseSession().writeObject(comp);

    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Vector computers = getSession().readAllObjects(Computer.class, new ExpressionBuilder().get("isMacintosh").equal(null));
        if (computers.size() == 0) {
            throw new TestErrorException("Field is empty instead of Null in Object Type Mapping");
        }
    }
}
