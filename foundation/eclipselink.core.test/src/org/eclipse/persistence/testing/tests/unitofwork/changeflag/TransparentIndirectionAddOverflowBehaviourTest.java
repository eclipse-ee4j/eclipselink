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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

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
public class TransparentIndirectionAddOverflowBehaviourTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public SalesPerson transfer;
    public FieldOffice clone;
    public FieldOffice clone2;

    public Resource resource;

    //stuff changed

    public TransparentIndirectionAddOverflowBehaviourTest() {
        setDescription("This test verifies that Tranparent Indirection works with change tracking. Specifically it verifies that mutliple calls to add have the same behavour as deferred detection.");
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
        this.clone.getSalespeople().remove(this.transfer);

        //For a set adding twice and removing once should remove the object.
        this.clone2.getSalespeople().add(this.transfer);
        this.clone2.getSalespeople().add(this.transfer);
        this.clone2.getSalespeople().remove(this.transfer);


        this.resource = (Resource)clone.getResources().iterator().next();
        clone.getResources().remove(this.resource);

        //for list adding twice and removing once should leave the object in the list
        clone2.getResources().add(this.resource);
        clone2.getResources().add(this.resource);
        clone2.getResources().remove(this.resource);


        uow.commit();
    }
    /*
     * Checks to see that the names of the updated version and the origional are the same
     */

    public void verify() {
        FieldOffice cachedOffice2 = (FieldOffice)getSession().readObject(this.clone2);
        SalesPerson cachedTransfer = (SalesPerson)getSession().readObject(this.transfer);
        Resource cachedResource = (Resource)getSession().readObject(this.resource);

        if (cachedOffice2.getSalespeople().contains(cachedTransfer)) {
            throw new TestErrorException("Failed to replicate Set behavior for double add when tracking changes");
        }

        if (!cachedOffice2.getResources().contains(cachedResource)) {
            throw new TestErrorException("Failed to replicate List behavior for double add when tracking changes");
        }
    }
}
