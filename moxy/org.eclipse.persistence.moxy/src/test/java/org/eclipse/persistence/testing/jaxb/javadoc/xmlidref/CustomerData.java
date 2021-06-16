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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlidref;

import java.util.Collection;

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
        if (!(obj instanceof CustomerData)) {
            return false;
        }

        CustomerData custData = (CustomerData) obj;
        return custData.customer.equals(this.customer) && custData.shipping.equals(this.shipping) && custData.invoice.equals(this.invoice) ;
    }
}
