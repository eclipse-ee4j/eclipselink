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

import java.util.Iterator;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;

public class SetReferencedObjectTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public FieldOffice fieldOfficeClone;
    public SalesPerson sales = null;
    public SalesPerson secondSales = null;

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
        Iterator iterator = uow.readAllObjects(SalesPerson.class).iterator();
        this.sales = (SalesPerson)iterator.next();
        this.secondSales = (SalesPerson)iterator.next();
        while ((this.secondSales.getFieldOffice() == this.sales.getFieldOffice()) && iterator.hasNext()) {
            this.secondSales = (SalesPerson)iterator.next();
        }
        this.fieldOfficeClone = this.sales.getFieldOffice();
        this.sales.setFieldOffice(this.secondSales.getFieldOffice());

    }

    public void verify() {
        if ((this.fieldOfficeClone.getSalespeople().contains(this.sales)) || (this.sales.getFieldOffice() == null) || (this.secondSales.getFieldOffice() == null) || (!this.secondSales.getFieldOffice().getSalespeople().contains(this.sales))) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to set the backPointer information");
        }
    }
}
