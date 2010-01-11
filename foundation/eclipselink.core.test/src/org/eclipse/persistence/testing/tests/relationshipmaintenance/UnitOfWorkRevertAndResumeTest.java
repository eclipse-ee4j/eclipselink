/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
