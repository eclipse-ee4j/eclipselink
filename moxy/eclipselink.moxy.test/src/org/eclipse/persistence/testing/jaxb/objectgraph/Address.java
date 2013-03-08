/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.objectgraph;

import org.eclipse.persistence.oxm.annotations.XmlNamedAttributeNode;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraph;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraphs;

@XmlNamedObjectGraphs({
    @XmlNamedObjectGraph(name="basic", attributeNodes={
        @XmlNamedAttributeNode("street")
    }),
    @XmlNamedObjectGraph(name="country", attributeNodes={
        @XmlNamedAttributeNode("country")
    })
})
public class Address {
    
    public String street;
    
    public String city;
    
    public String country;
    
    public boolean equals(Object obj) {
        Address addr = (Address)obj;
        
        return (street == addr.street || street.equals(addr.street)) 
                && (city == addr.city || city.equals(addr.city))
                && (country == addr.country || country.equals(addr.country));
    }

}
