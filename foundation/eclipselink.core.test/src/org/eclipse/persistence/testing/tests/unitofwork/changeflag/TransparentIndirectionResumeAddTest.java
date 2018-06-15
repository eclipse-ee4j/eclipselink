/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.relationshipmaintenance.FieldOffice;
import org.eclipse.persistence.testing.models.relationshipmaintenance.SalesPerson;


/**
 * Test adding to indirect collections on new objects after a commit and resume.
 */
public class TransparentIndirectionResumeAddTest extends TransactionalTestCase {

    public TransparentIndirectionResumeAddTest() {
        setDescription("Test adding to indirect collections on new objects after a commit and resume.");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        FieldOffice office = new FieldOffice();
        SalesPerson person = new SalesPerson();
        person.setName("Bob");
        person.setFieldOffice(office);
        office.getSalespeople().add(person);

        uow.registerNewObject(office);

        uow.commitAndResume();

        person = new SalesPerson();
        person.setName("Joe");
        person.setFieldOffice(office);
        office.getSalespeople().add(person);

        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in clone.");
        }
        uow.commit();

        office = (FieldOffice)getSession().readObject(office);
        person = (SalesPerson)getSession().readObject(person);
        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in cache.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        office = (FieldOffice)getSession().readObject(office);
        person = (SalesPerson)getSession().readObject(person);
        if (!office.getSalespeople().contains(person)) {
            throwError("Person not added in cache.");
        }
    }
}
