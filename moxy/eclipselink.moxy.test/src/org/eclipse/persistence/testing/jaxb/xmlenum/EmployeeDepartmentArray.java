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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="employee")
public class EmployeeDepartmentArray {
    public String name;

    @XmlElement(name="department-number")
    public Department[] deps;
    
    public boolean equals(Object o) {
        if(!(o instanceof EmployeeDepartmentArray) || o == null) {
            return false;
        } 
        if(((EmployeeDepartmentArray)o).deps.length != (this.deps.length)){
        	return false;        	           
        }
        for(int i=0;i<deps.length; i++){
        	if(!deps[i].equals(((EmployeeDepartmentArray)o).deps[i])){
        		return false;
        	}
        }
        return true;
    }
    
    public String toString() {
        return "EMPLOYEE(" + deps + ")";
    }        
}