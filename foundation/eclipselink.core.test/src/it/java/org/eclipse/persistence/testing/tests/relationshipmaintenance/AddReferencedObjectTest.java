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
package org.eclipse.persistence.testing.tests.relationshipmaintenance;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;

import java.util.Vector;

public class AddReferencedObjectTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public FieldOffice fieldOfficeClone;
    public FieldOffice secondOfficeClone;
    public SalesPerson sales = null;

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Vector offices = uow.readAllObjects(FieldOffice.class);
        this.fieldOfficeClone = (FieldOffice)offices.get(0);
        this.secondOfficeClone = (FieldOffice)offices.get(1);
        this.sales = (SalesPerson)this.fieldOfficeClone.getSalespeople().iterator().next();
        this.secondOfficeClone.getSalespeople().add(sales);
        uow.commit();
    }

    @Override
    public void verify() {
        if ((!this.secondOfficeClone.getSalespeople().contains(this.sales)) || (this.sales.getFieldOffice() == null) || (this.fieldOfficeClone.getSalespeople().contains(this.sales))) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to set the backPointer information");
        }
    }
}
