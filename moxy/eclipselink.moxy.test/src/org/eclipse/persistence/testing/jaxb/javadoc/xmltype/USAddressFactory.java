/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;


public class USAddressFactory {

    public static USAddress getUSAddress(){
        return new USAddress("Mark Baker", "23 Elm St",
           "Dayton", "OH", 90952);



 /*   public boolean equals(Object obj) {
        USAddress addr = (USAddress)obj;
        return name.equals(addr.name) && city.equals(addr.city) && street.equals(addr.street) && state.equals(addr.state);
    }
    */
}
}

