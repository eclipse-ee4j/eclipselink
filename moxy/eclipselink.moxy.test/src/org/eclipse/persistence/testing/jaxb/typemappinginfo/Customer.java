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
//     Matt MacIvor -  January, 2010
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import javax.xml.bind.annotation.XmlRootElement;

public class Customer {

    public String firstName;
    public String lastName;
    public String phoneNumber;

    public boolean equals(Object obj) {
        if(!(obj instanceof Customer)) {
            return false;
        }
        Customer cust = (Customer)obj;

        return firstName.equals(cust.firstName) && lastName.equals(cust.lastName) && phoneNumber.equals(cust.phoneNumber);
    }

}
