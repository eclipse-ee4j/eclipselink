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

public class SelectSimpleMemberOfTest extends JPQLTestCase {
    public SelectSimpleMemberOfTest() {
    }

    public void setup() {
        String ejbqlString;
        ejbqlString = "SELECT OBJECT(p) FROM Employee e, Project p ";
        ejbqlString = ejbqlString + "WHERE p.teamLeader NOT MEMBER OF e.manager.managedEmployees";
        setEjbqlString(ejbqlString);

        super.setup();
    }

    public void verify() {
    }
}
