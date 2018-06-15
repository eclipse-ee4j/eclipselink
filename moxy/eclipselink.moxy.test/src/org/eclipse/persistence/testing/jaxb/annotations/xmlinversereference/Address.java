/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class Address implements Linkable {

    public String street;

    private Customer customer;

    @XmlInverseReference(mappedBy="address")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    private String link;

    @Override
    public String getLink() {
        // TODO Auto-generated method stub
        return link;
    }

    @Override
    public void setLink(String link) {
        // TODO Auto-generated method stub
        this.link = link;
    }

}
