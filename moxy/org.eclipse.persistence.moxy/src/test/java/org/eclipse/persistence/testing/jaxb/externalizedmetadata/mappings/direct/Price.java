/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - February 17/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

public class Price {
    public java.math.BigDecimal price;
    public String currency;

    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    java.math.BigDecimal getPrice() {
        wasGetCalled = true;
        return price;
    }

    void setPrice(java.math.BigDecimal price) {
        wasSetCalled = true;
        this.price = price;
    }


    public boolean equals(Object obj) {
        Price pObj;
        try {
            pObj = (Price) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if (price == null) {
            if (pObj.price != null) {
                return false;
            }
            return this.currency.equals(pObj.currency);
        } else {
            if (pObj.price == null) {
                return false;
            }
        }
        return this.currency.equals(pObj.currency) && this.price.equals(pObj.price);
    }
}
