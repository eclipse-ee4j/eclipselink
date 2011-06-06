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
