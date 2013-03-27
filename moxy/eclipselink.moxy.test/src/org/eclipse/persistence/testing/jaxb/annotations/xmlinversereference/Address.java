/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
 ******************************************************************************/
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