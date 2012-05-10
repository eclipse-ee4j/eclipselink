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
package org.eclipse.persistence.testing.tests.relationshipmaintenance;

import java.util.Iterator;
import java.util.Vector;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;

public class SetToNullTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public FieldOffice fieldOfficeClone;
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
        Vector salesPeople = uow.readAllObjects(SalesPerson.class);
        for (Iterator iterator = salesPeople.iterator(); iterator.hasNext();) {
            this.sales = (SalesPerson)iterator.next();
            if (sales.getFieldOffice() != null) {
                this.fieldOfficeClone = this.sales.getFieldOffice();
                this.sales.setFieldOffice(null);
                uow.commit();
                return;
            }
        }
        throw new org.eclipse.persistence.testing.framework.TestErrorException("Test failed to run correctly as there are no sales people associated with a field office in the cache");

    }

    public void verify() {
        if ((this.fieldOfficeClone.getSalespeople().contains(this.sales)) || (this.sales.getFieldOffice() != null)) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to set the backPointer information");
        }
    }
}
