/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlelements;

import javax.xml.bind.annotation.*;

@XmlType(name="address-type")
public class Address {
    
    public String street;
    public String city;
    
    
    public boolean equals(Object obj) {
        Address addr = (Address)obj;
        return street.equals(addr.street) && city.equals(addr.city);
    }
}

