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
package org.eclipse.persistence.testing.tests.jpql;


// Java imports
import java.util.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleApostropheTest extends JPQLTestCase {
    public void setup() {
        Vector addresses = getSomeAddresses();
        Address address = null;
        String ejbqlString = null;

        //Find the desired address
        Iterator addressesIterator = addresses.iterator();
        while (addressesIterator.hasNext()) {
            address = (Address)addressesIterator.next();
            if (address.getStreet().indexOf("Lost") != -1) {
                break;
            }
        }

        setOriginalOject(address);

        //test the apostrophe
        ejbqlString = "SELECT OBJECT(address) FROM Address address WHERE ";
        ejbqlString = ejbqlString + "address.street = '234 I''m Lost Lane'";
        setEjbqlString(ejbqlString);
        setReferenceClass(Address.class);
        super.setup();
    }
}
