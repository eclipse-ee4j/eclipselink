/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.sun.prefixmapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="person", namespace="someuri")
@XmlType(name="person-type", namespace="someuri")
public class Person {
    
    public String firstName;
    public String lastName;
    public String address;
    
    public boolean equals(Object obj) {
        Person p = (Person)obj;
        
        return p.firstName.equals(firstName) && p.lastName.equals(lastName) && p.address.equals(address);
    }

}
