/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.ManualVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CommitOrderTest extends ManualVerifyTestCase {
    public Map oldDescriptors;

    public CommitOrderTest() {
        setDescription("Check that the commit order is consistent and order by 1-1 constraints.");
    }

    public void printClasses(List<Class<?>> classes) {
        for (Class<?> aClass : classes) {
            System.out.print(aClass.getSimpleName());
            System.out.print(", ");
        }
        System.out.println(":");
    }

    @Override
    public void reset() {
        getSession().getProject().setDescriptors(this.oldDescriptors);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
    }

    @Override
    public void setup() {
        this.oldDescriptors = getSession().getDescriptors();
    }

    @Override
    public void test() {
        getSession().setLogLevel(SessionLog.FINE);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new Hashtable());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new EmployeeSystem().addDescriptors(getDatabaseSession());
        printClasses(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new Hashtable());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InsuranceSystem().addDescriptors(getDatabaseSession());
        printClasses(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new Hashtable());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new org.eclipse.persistence.testing.models.ownership.OwnershipSystem().addDescriptors(getDatabaseSession());
        printClasses(getAbstractSession().getCommitManager().getCommitOrder());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getProject().setDescriptors(new Hashtable());
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
        new InheritanceSystem().addDescriptors(getDatabaseSession());
        printClasses(getAbstractSession().getCommitManager().getCommitOrder());
    }
}
