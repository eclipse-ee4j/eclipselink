/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.insurance.*;


public class CascadeWithoutIMOnRemote extends TestCase {
    protected Employee emp1, emp2;
    protected PolicyHolder holder1, holder2;

    public CascadeWithoutIMOnRemote() {
        setDescription("Tests if remote refresh cascades parts without maintain cache (on query) correctly.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() throws Exception {

        emp1 = (Employee)getSession().readObject(Employee.class);
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(emp1);
        query.dontMaintainCache();
        query.dontCascadeParts();
        emp2 = (Employee)getSession().executeQuery(query);

        holder1 = (PolicyHolder)getSession().readObject(PolicyHolder.class);
        ReadObjectQuery query2 = new ReadObjectQuery();
        query2.setSelectionObject(holder1);
        query2.dontMaintainCache();
        query2.dontCascadeParts();
        holder2 = (PolicyHolder)getSession().executeQuery(query2);
    }

    public void verify() throws Exception {

        if ((emp1 == emp2) || (emp1.getAddress() != emp2.getAddress()) || 
            (emp1.getPhoneNumbers() != (emp2.getPhoneNumbers()))) {
            throw new TestWarningException("Cascade part with no IM on remote with indirection fails on remote.");
        }

        if ((holder1 == holder2) || (holder1.getAddress() != holder2.getAddress()) || 
            (holder1.getPolicies() != (holder2.getPolicies()))) {
            throw new TestWarningException("Cascade part with no IM on remote without indirection fails on remote.");
        }
    }
}
