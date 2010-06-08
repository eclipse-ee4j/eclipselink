/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - April 14/2010 - 2.1 - Initial implementation
******************************************************************************/
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