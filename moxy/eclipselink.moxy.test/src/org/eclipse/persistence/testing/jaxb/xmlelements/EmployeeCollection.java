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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlelements;

import javax.xml.bind.annotation.*;
import java.util.Collection;

@XmlRootElement(name="employee-data")
public class EmployeeCollection 
{
    @XmlElement(name="id")
    public int id;
    
    @XmlElements({@XmlElement(name="integer", type=Integer.class), @XmlElement(name="address", type=Address.class), @XmlElement(name="string", type=String.class)})
    public Collection<Object> choice;

    public String toString()
    {
        return "EMPLOYEE: " + id + "\n choice=" + choice;
    }

    public boolean equals(Object object) {
        EmployeeCollection emp = ((EmployeeCollection)object);
        return emp.id == this.id && emp.choice.equals(choice);
    }
}
