/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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