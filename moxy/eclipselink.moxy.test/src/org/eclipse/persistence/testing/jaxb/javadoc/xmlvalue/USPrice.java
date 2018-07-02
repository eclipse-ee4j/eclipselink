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
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue;

import javax.xml.bind.annotation.*;

/*
 * Example 1: Map a class to XML Schema simpleType
 */
@XmlRootElement(name = "us-price")
public class USPrice {

    @XmlValue
    public double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean equals(Object o) {
        if (!(o instanceof USPrice) || o == null) {
            return false;
        } else {
            return ((USPrice) o).price == this.price;
        }
    }

}
