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
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class PhoneNumber {

    @XmlValue
    public String number;

    @XmlAttribute
    public String areaCode;

    public boolean equals(Object obj) {
        if(!(obj instanceof PhoneNumber)) {
            return false;
        } else {
            if(areaCode == null){
                if(((PhoneNumber)obj).areaCode != null){
                    return false;
                }
            }else if(!this.areaCode.equals(((PhoneNumber)obj).areaCode)){
                return false;
            }
            return this.number.equals(((PhoneNumber)obj).number);
        }
    }
}
