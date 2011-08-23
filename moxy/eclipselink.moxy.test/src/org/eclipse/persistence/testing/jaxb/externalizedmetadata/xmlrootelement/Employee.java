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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlrootelement;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class Employee {
    public String firstName;
    public String lastName;

    public void setMyInt(int newInt) {}
    public int getMyInt() {
        return 66;
    }
    
    public boolean equals(Object obj){
    	if(obj instanceof Employee){
    		Employee empObj = (Employee)obj;
    		return firstName.equals(empObj.firstName) && lastName.equals(empObj.lastName);
    	}
    	return false;
    }
}
