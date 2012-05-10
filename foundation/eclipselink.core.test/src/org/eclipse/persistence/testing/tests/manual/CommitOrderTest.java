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
package org.eclipse.persistence.testing.tests.manual;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;

public class CommitOrderTest extends ManualVerifyTestCase {
    public Map oldDescriptors;

    public CommitOrderTest() {
        setDescription("Check that the commit order is consistent and order by 1-1 constraints.");
    }

    public void printClasses(List classes) {
        for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
            System.out.print(org.eclipse.persistence.internal.helper.Helper.getShortClassName((Class)iterator.next()));
            System.out.print(", ");
        }
        System.out.println(":");
    }

    public void reset() {
        getSession().getProject().setDescriptors(this.oldDescriptors);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
    }

    public void setup() {
        this.oldDescriptors = getSession().getDescriptors();
    }

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
