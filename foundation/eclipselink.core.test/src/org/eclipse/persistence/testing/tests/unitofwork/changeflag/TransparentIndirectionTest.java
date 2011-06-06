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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Resource;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;


/**
 * This test checks on a previous problem TopLink had with merging clones and
 * serialization.  If an existing object was serialized and deserialized and another
 * reference added to that object.  Then the object was serialized and deserialized
 * and deepMergeClone'd into a unit of work and committed.
 * When the origional object was then serialized and deserialized and modified, then serialized
 * and deserialized and deepMergeClone'd with another unit of work and committed the changes are
 * not saved
 */
public class TransparentIndirectionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public SalesPerson transfer;
    public SalesPerson transfer2;
    public FieldOffice clone;
    public FieldOffice clone2;

    public Resource resource;

    //stuff changed

    public TransparentIndirectionTest() {
        setDescription("This test verifies that Tranparent Indirection works with change tracking");
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();
    }

    /*
     * This test creates an object and registers it with a unit of work.  It then serializes that
     * object and deserializes it.  Adds an object onto the origional then performs serialization
     * sequence again.  Then deepMergeClone is attempted and the results are compared to verify that
     * the merge worked.
     */

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.clone = 
                (FieldOffice)uow.readObject(FieldOffice.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("location").get("city").equal("Toronto"));
        this.clone2 = 
                (FieldOffice)uow.readObject(FieldOffice.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("location").get("city").equal("Ottawa"));


        this.transfer = (SalesPerson)clone.getSalespeople().iterator().next();
        this.transfer2 = (SalesPerson)clone2.getSalespeople().iterator().next();

        this.clone2.getSalespeople().remove(this.transfer2);
        this.transfer2.setFieldOffice(null);

        HashSet sales = new HashSet(this.clone2.getSalespeople());
        sales.add(this.transfer);


        this.clone.getSalespeople().remove(this.transfer);
        this.clone.getSalespeople().add(this.transfer2);
        this.transfer2.setFieldOffice(this.clone);

        this.clone2.setSalespeople(sales);
        this.transfer.setFieldOffice(this.clone2);

        this.resource = (Resource)clone.getResources().iterator().next();
        clone.getResources().remove(this.resource);
        ArrayList list = new ArrayList(clone2.getResources());
        list.add(this.resource);
        clone2.setResources(list);
        this.resource.setOffice(this.clone2);
        uow.commit();
    }
    /*
     * Checks to see that the names of the updated version and the origional are the same
     */

    public void verify() {
        FieldOffice cachedOffice = (FieldOffice)getSession().readObject(this.clone);
        FieldOffice cachedOffice2 = (FieldOffice)getSession().readObject(this.clone2);
        SalesPerson cachedTransfer = (SalesPerson)getSession().readObject(this.transfer);
        SalesPerson cachedTransfer2 = (SalesPerson)getSession().readObject(this.transfer2);
        Resource cachedResource = (Resource)getSession().readObject(this.resource);

        if (cachedOffice.getSalespeople().contains(cachedTransfer)) {
            throw new TestErrorException("Failed to track changes from indirect list with transparent indirection");
        }
        if (!cachedOffice2.getSalespeople().contains(cachedTransfer)) {
            throw new TestErrorException("Failed to track changes when new entire collection set with transparent indirection");
        }

        if (cachedOffice2.getSalespeople().contains(cachedTransfer2)) {
            throw new TestErrorException("Failed to merge changes from indirect list with transparent indirection");
        }
        if (!cachedOffice.getSalespeople().contains(cachedTransfer2)) {
            throw new TestErrorException("Failed to merge changes when new entire collection set with transparent indirection");
        }

        if (cachedOffice.getResources().contains(cachedResource)) {
            throw new TestErrorException("Failed to track changes from indirect list without indirection");
        }
        if (!cachedOffice2.getResources().contains(cachedResource)) {
            throw new TestErrorException("Failed to track changes when new entire collection set without indirection");
        }
    }
}
