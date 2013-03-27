/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4.2 - Initial Implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlelements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

@XmlRootElement(name="customer")
public class CustomerPredicate {
    @XmlElements({
        @XmlElement(type=Address.class),
        @XmlElement(type=Link.class)
    })
    @XmlPaths({
        @XmlPath("address"),
        @XmlPath("address[@rel=\"self\"]")
    })
    public Object address;
    
    public boolean equals(Object obj) {
        CustomerPredicate cust = (CustomerPredicate)obj;
        return address == cust.address || address.equals(address);
    }
}