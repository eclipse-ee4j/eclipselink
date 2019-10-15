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
package org.eclipse.persistence.testing.tests.interfaces;

import java.util.List;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

/**
 * This Test was implemented to test the preInsert of Variable one to one mappings when the objects
 * are not privately owned, previously Variable One to One mappings attempted to rewrite the non privately
 * owned attribute.
 */
public class VariableOneToOneNonPrivatelyOwnedTest extends TransactionalTestCase {
    public Contact contact;
    public ContactHolder origional;

    public void test() {
        List<Phone> phones = getSession().readAllObjects(Phone.class);
        for (Phone phone : phones) {
            if (phone.getEmp() != null) {
                this.contact = phone;
                break;
            }
        }
        this.origional = (ContactHolder)this.contact.getEmp().clone();
        ContactHolder holder = this.contact.getEmp();
        if (holder instanceof Employee) {
            ((Employee)holder).setName("Bobby Vallence");
        } else {
            ((Company)holder).setName("Nowhere Company");
        }
        getDatabaseSession().writeObject(this.contact);

    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        this.contact = (Contact)getSession().readObject(this.contact);
        if (!compareObjects(this.contact.getEmp(), this.origional)) {
            throw new TestErrorException("Write Object wrote non privately owned data to the database");
        }

    }
}
