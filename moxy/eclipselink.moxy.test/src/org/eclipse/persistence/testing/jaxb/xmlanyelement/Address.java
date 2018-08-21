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
package org.eclipse.persistence.testing.jaxb.xmlanyelement;
import javax.xml.bind.annotation.*;

@XmlRootElement(name="address")
public class Address {

    public String street;
    public String city;
    public String country;

    public boolean equals(Object obj) {
        if(!(obj instanceof Address)) {
            return false;
        }
        Address addr = (Address)obj;
        return ((addr.street ==null && street==null) || ( addr.street.equals(street)))
          &&((addr.city ==null && city==null) || ( addr.city.equals(city)))
          &&((addr.country ==null && country==null) || ( addr.country.equals(country)));
    }

}
