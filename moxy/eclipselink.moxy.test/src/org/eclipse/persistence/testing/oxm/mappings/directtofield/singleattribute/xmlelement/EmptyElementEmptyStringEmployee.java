/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-06-19 14:33:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelement;

public class EmptyElementEmptyStringEmployee {

    private String id;

    public EmptyElementEmptyStringEmployee() {
        super();
    }

    public String getID() {
        return id;
    }

    public void setID(String newID) {
        id = newID;
    }

    public boolean equals(Object object) {
        try {
            EmptyElementEmptyStringEmployee employee = (EmptyElementEmptyStringEmployee) object;
            if (getID() == null && employee.getID() == null) {
                return true;
			} else if (getID() == null && employee.getID() != null) {
				return false;
			} else if (getID() != null && employee.getID() == null) {
			    return false;
			}
			return this.getID().equals(employee.getID());
        } catch(ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "Employee: ID=[" + this.getID() + "]";
    }
}
