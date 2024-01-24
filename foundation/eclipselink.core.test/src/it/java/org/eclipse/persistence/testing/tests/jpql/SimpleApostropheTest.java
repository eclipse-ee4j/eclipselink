/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;


// Java imports
import java.util.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleApostropheTest extends JPQLTestCase {
    @Override
    public void setup() {
        Vector addresses = getSomeAddresses();
        Address address = null;
        String ejbqlString = null;

        //Find the desired address
        for (Object o : addresses) {
            address = (Address) o;
            if (address.getStreet().contains("Lost")) {
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
