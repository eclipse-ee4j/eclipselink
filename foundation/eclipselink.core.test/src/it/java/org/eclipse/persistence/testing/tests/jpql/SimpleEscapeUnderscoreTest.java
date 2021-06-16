/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleEscapeUnderscoreTest extends JPQLTestCase {
    private Address addressWithUnderscore;

    public void setup() {
        String ejbqlString = null;

        addressWithUnderscore = new Address();
        addressWithUnderscore.setCity("Perth");
        addressWithUnderscore.setCountry("Canada");
        addressWithUnderscore.setProvince("ONT");
        addressWithUnderscore.setPostalCode("Y3Q2N9");
        addressWithUnderscore.setStreet("234 Wandering _Way");
        this.getDatabaseSession().insertObject(addressWithUnderscore);

        setOriginalOject(addressWithUnderscore);

        //test the apostrophe
        ejbqlString = "SELECT OBJECT(address) FROM Address address WHERE ";
        // \ is always treated as escape in MySQL.  Therefore ESCAPE '\' is considered a syntax error
        if (getSession().getLogin().getPlatform().isMySQL() || getSession().getLogin().getPlatform().isPostgreSQL()) {
            ejbqlString = ejbqlString + "address.street LIKE '234 Wandering $_Way' ESCAPE '$'";
        } else {
            ejbqlString = ejbqlString + "address.street LIKE '234 Wandering \\_Way' ESCAPE '\\'";
        }
        setEjbqlString(ejbqlString);
        setReferenceClass(Address.class);
        super.setup();
    }

    public void reset() {
        //delete the address we inserted for this test
        getDatabaseSession().deleteObject(addressWithUnderscore);
        super.reset();
    }
}
