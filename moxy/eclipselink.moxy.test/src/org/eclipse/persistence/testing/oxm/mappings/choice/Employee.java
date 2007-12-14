/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.choice;

public class Employee {
    public String name;
    public Object choice;
    public String phone;
    
    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;
        return emp.name.equals(name) && emp.choice.equals(choice) && emp.phone.equals(phone);
    }
}

