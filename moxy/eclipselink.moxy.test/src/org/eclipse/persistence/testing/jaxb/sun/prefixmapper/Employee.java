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

@XmlRootElement(namespace="someuri")
@XmlType(name="emp-type", namespace="someuri")
public class Employee extends Person {

    public int employeeId;
    
    public Person manager;
    
    public boolean equals(Object obj) {
        Employee e = (Employee)obj;
        
        return super.equals(e) && employeeId == e.employeeId && (manager == e.manager || manager.equals(e.manager));
    }
}
