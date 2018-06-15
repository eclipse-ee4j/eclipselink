/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

public class SelectSimpleMemberOfTest extends JPQLTestCase {
    public SelectSimpleMemberOfTest() {
    }

    public void setup() {
        String ejbqlString;
        ejbqlString = "SELECT OBJECT(proj) FROM Employee emp, Project proj ";
        ejbqlString = ejbqlString + "WHERE proj.teamLeader NOT MEMBER OF emp.manager.managedEmployees";
        setEjbqlString(ejbqlString);

        super.setup();
    }

    public void verify() {
    }
}
