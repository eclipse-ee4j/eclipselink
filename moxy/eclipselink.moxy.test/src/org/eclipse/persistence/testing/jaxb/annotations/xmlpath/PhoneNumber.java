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
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import javax.xml.bind.annotation.XmlValue;

public class PhoneNumber {

    @XmlValue
    public String number;

    public boolean equals(Object obj) {
        if(!(obj instanceof PhoneNumber)) {
            return false;
        } else {
            return this.number.equals(((PhoneNumber)obj).number);
        }
    }
}
