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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

// map class to anonymous simple type.
@XmlType(name="")
public class USPrice {

    @XmlValue
    public BigInteger price;

    @Override
    public boolean equals(Object obj) {
        if (null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        USPrice test = (USPrice) obj;
        if (null == price) {
            return null == test.price;
        } else {
            return price.equals(test.price);
        }

    }

}
