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
package org.eclipse.persistence.testing.tests.relationshipmaintenance;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Customer;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;

public class UnitOfWorkRevertAndResumeTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Customer customer;
    public SalesPerson sales = null;

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        sales = (SalesPerson)uow.readObject(SalesPerson.class);
        sales.getCustomers().add(new Customer());
        uow.revertAndResume();
        customer = new Customer();
        customer.setName("Andy Fellows");
        customer = (Customer)uow.registerObject(customer);
        sales.getCustomers().add(customer);
    }

    public void verify() {
        if (!this.customer.getSalespeople().contains(this.sales)) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to set the backPointer information, after revertAndResume");
        }
    }
}
