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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldLocation;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;

public class SetUnReferencedObjectTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public FieldOffice fieldOfficeClone;
    public SalesPerson sales = null;

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.fieldOfficeClone = new FieldOffice();
        this.fieldOfficeClone.setLocation(FieldLocation.example2());
        uow.registerObject(this.fieldOfficeClone);
        uow.commit();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.sales = (SalesPerson)uow.readObject(SalesPerson.class);
        this.fieldOfficeClone = (FieldOffice)uow.readObject(this.fieldOfficeClone);
        this.sales.setFieldOffice(this.fieldOfficeClone);
        uow.commit();

    }

    public void verify() {
        if ((!this.fieldOfficeClone.getSalespeople().contains(this.sales)) || (this.sales.getFieldOffice() == null)) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to set the backPointer information");
        }
    }
}
