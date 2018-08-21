/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;

@XmlType(name="USAddressType", factoryClass=USAddressFactory.class,factoryMethod="getUSAddress")

public class USAddress {

    private String city, name, state, street;
    private int zip;

    public USAddress(String name, String street, String city, String state, int zip){
        this.name=name;
        this.street=street;
        this.city=city;
        this.state=state;
        this.zip=zip;
    }


 /*   public boolean equals(Object obj) {
        USAddress addr = (USAddress)obj;
        return name.equals(addr.name) && city.equals(addr.city) && street.equals(addr.street) && state.equals(addr.state);
    }
    */
}

