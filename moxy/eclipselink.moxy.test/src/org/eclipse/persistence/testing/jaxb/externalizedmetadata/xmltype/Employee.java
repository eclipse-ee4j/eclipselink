/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="my-employee-type", propOrder= {"id", "firstName", "lastName"})
public class Employee {
    public String firstName = "";
    public String lastName = "";
    public int id = -1;
    
    @XmlTransient
    public boolean fromFactoryMethod;
    
    public boolean equals(Object o) {
        Employee obj;
        try {
            obj = (Employee) o;
        } catch (ClassCastException cce) {
            return false;
        }
        if (id != obj.id) {
            return false;
        }
        if (!firstName.equals(obj.firstName)) {
            return false;
        }
        if (!lastName.equals(obj.lastName)) {
            return false;
        }
        return true;
    }
}
