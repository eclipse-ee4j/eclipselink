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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelement;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement()
public class USPriceNillable {
    @XmlElement(nillable = true)
    Double currency;

    public Double getCurrency() {
        return currency;
    }

    public void setCurrency(Double name) {
        this.currency = name;
    }

    public boolean equals(Object object) {
        USPriceNillable pNillable = ((USPriceNillable) object);

        if (object == null)
            return false;

        if (pNillable.currency == null && this.currency != null)
            return false;
        if (pNillable.currency != null && this.currency == null)
            return false;
        if (pNillable.currency == null && this.currency == null)
            return true;

        if (pNillable.currency != null && this.currency != null) {
            return pNillable.currency.equals(this.currency);
        } else {
            return false;
        }
    }
}
