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
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic.withstatic;

import javax.xml.bind.annotation.XmlAttribute;

public class Address {

    @XmlAttribute
    public String street;
    
    public String city;
    
    public boolean equals(Object obj) {
       Address address = (Address)obj;
       
       return street.equals(address.street) && city.equals(address.city);
       
    }
}
