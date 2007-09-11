/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype;

public class Employee extends org.eclipse.persistence.testing.oxm.mappings.keybased.Employee {
	public Address address;
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Employee)) {
			return false;
		}
		Employee emp = (Employee) obj;
        if (this.address == null) {
            return emp.address == null;
        }
        if (emp.address == null) {
            return false;
        }
		return this.address.equals(emp.address);
	}
}