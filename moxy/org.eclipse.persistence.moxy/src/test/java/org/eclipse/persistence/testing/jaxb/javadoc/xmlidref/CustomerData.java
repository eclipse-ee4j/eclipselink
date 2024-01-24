/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlidref;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="customer-data")
public class CustomerData {

   @XmlElement(name="customer")
    public Customer customer;

   @XmlElement(name="shipping")
    public Shipping shipping;

    @XmlElement(name="invoice")
    public Invoice invoice;

    public boolean equals(Object obj) {
        if (!(obj instanceof CustomerData custData)) {
            return false;
        }

        return custData.customer.equals(this.customer) && custData.shipping.equals(this.shipping) && custData.invoice.equals(this.invoice) ;
    }
}
