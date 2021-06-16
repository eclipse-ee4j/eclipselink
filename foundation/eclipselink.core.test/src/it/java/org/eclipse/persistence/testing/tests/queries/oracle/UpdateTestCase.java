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
package org.eclipse.persistence.testing.tests.queries.oracle;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.mapping.*;

public class UpdateTestCase extends TestCase {
    DescriptorQueryManager qm;
    Employee emp;
    static String HINT_STRING = "/*+ RULE */";

    public UpdateTestCase() {
        setName("Update Test");
        setDescription("Tests the use of a hint in an update");
    }

    public void setup() {
        ClassDescriptor employeeDescriptor = getSession().getDescriptor(Employee.class);
        qm = employeeDescriptor.getQueryManager();

        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        qm.setUpdateQuery(updateQuery);
        emp = new Employee();
        emp.firstName = "Matt";
        emp.lastName = "MacIvor";
        emp.sex = "male";
        getAbstractSession().writeObject(emp);
    }

    public void reset() {
        qm.setUpdateQuery(null);
        getAbstractSession().deleteObject(emp);
    }

    public void test() {
        DatabaseQuery updateQuery = qm.getUpdateQuery();
        updateQuery.setHintString(HINT_STRING);
        Employee emp =
            (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("lastName").equal("MacIvor"));
        emp.firstName = "Matthew";
        getAbstractSession().updateObject(emp);
    }
}
