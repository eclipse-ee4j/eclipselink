/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="employee")
public class Employee {

    public String name;
    
    @XmlAttribute
    @XmlJavaTypeAdapter(ListToStringAdapter.class)
    public List<String> responsibilities;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }
        
        Employee emp = (Employee)obj;
        return emp.name.equals(this.name)&& emp.responsibilities.equals(this.responsibilities); 
    }
}
