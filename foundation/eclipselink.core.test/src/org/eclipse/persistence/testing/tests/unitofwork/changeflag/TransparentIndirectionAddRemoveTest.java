/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;


/**
 * Test the add/remove optimization of indirect collections with change tracking.
 * These should not trigger the collection to be instantiated.
 */
public class TransparentIndirectionAddRemoveTest extends TransactionalTestCase {

    private Boolean lazyInstantiationForSalesPeople = null;

    public TransparentIndirectionAddRemoveTest() {
        setDescription("Test the add/remove optimization of indirect collections with change tracking.");
    }

    public void setup(){
        CollectionMapping mapping = (CollectionMapping)getSession().getClassDescriptor(FieldOffice.class).getMappingForAttributeName("salespeople");
        lazyInstantiationForSalesPeople = mapping.shouldUseLazyInstantiationForIndirectCollection();
        mapping.setUseLazyInstantiationForIndirectCollection(true);
    }

    public void test() {
        testAddRemove();
        testSetUnset();
    }

    public void testAddRemove() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        FieldOffice office =
            (FieldOffice)uow.readObject(FieldOffice.class, new ExpressionBuilder().get("location").get("city").equal("Toronto"));

        SalesPerson person = new SalesPerson();
        person.setName("Bob");
        person.setFieldOffice(office);
        office.getSalespeople().add(person);

        uow.commit();
        if (((IndirectContainer)office.getSalespeople()).isInstantiated()) {
            throwError("Sales people indirect collection should not be instantiated on add.");
        }

        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in clone.");
        }
        office = (FieldOffice)getSession().readObject(office);
        person = (SalesPerson)getSession().readObject(person);
        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in cache.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        uow = getSession().acquireUnitOfWork();
        office = (FieldOffice)uow.readObject(office);
        person = (SalesPerson)uow.readObject(person);

        office.getSalespeople().remove(person);

        uow.commit();
        if (((IndirectContainer)office.getSalespeople()).isInstantiated()) {
            throwError("Sales people indirect collection should not be instantiated on remove.");
        }

        if (office.getSalespeople().contains(person)) {
            throwError("Person not removed in clone.");
        }
        office = (FieldOffice)getSession().readObject(office);
        person = (SalesPerson)getSession().readObject(person);
        if (office.getSalespeople().contains(person)) {
            throwError("Person not removed in cache.");
        }
    }

    public void testSetUnset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        FieldOffice office =
            (FieldOffice)uow.readObject(FieldOffice.class, new ExpressionBuilder().get("location").get("city").equal("Toronto"));

        SalesPerson person = new SalesPerson();
        person.setName("Bob");
        person = (SalesPerson)uow.registerObject(person);
        person.setFieldOffice(office);

        uow.commit();
        if (((IndirectContainer)office.getSalespeople()).isInstantiated()) {
            throwError("Sales people indirect collection should not be instantiated on add.");
        }

        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in clone.");
        }
        office = (FieldOffice)getSession().readObject(office);
        person = (SalesPerson)getSession().readObject(person);
        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in cache.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        uow = getSession().acquireUnitOfWork();
        office = (FieldOffice)uow.readObject(office);
        person = (SalesPerson)uow.readObject(person);

        person.setFieldOffice(null);

        uow.commit();
        if (((IndirectContainer)office.getSalespeople()).isInstantiated()) {
            throwError("Sales people indirect collection should not be instantiated on remove.");
        }

        if (office.getSalespeople().contains(person)) {
            throwError("Person not removed in clone.");
        }
        office = (FieldOffice)getSession().readObject(office);
        person = (SalesPerson)getSession().readObject(person);
        if (office.getSalespeople().contains(person)) {
            throwError("Person not removed in cache.");
        }
    }

    public void reset(){
        CollectionMapping mapping = (CollectionMapping)getSession().getClassDescriptor(FieldOffice.class).getMappingForAttributeName("salespeople");
        mapping.setUseLazyInstantiationForIndirectCollection(lazyInstantiationForSalesPeople);
    }

}
