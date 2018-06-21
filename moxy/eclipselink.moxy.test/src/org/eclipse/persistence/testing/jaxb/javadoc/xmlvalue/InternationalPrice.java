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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="global-price")
public class InternationalPrice {

    @XmlValue
    public double price;

    @XmlAttribute
    public String currency;

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public boolean equals(Object o) {
        if(!(o instanceof InternationalPrice) || o == null) {
            return false;
        } else {
            return ((InternationalPrice)o).price == this.price && ((InternationalPrice)o).currency.equals(this.currency);
        }
    }

}
