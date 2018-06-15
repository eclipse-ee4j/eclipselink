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
//     bdoughan - April 14/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidrefs;

import javax.xml.bind.annotation.XmlID;

public class PhoneNumber {

    @XmlID
    public String id;

    public boolean equals(Object object) {
        PhoneNumber phone = null;
        try {
            if(null == object) {
                return false;
            }
            PhoneNumber testPhoneNumber= (PhoneNumber) object;
            if(null == id) {
                return null == testPhoneNumber.id;
            } else {
                return id.equals(testPhoneNumber.id);
            }
        } catch(ClassCastException ex) {
            return false;
        }
    }

}
