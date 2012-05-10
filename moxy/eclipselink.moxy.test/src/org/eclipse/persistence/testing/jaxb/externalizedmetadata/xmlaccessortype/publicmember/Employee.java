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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.publicmember;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="employee-type")
public class Employee {
    public String firstName;
    public String lastName;
    private int id;
    public Employee(){
    }
    
    public Employee(int id){
    	this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public boolean getIsSet() {
        return true;
    }

    public void setIsSet(boolean isSet) {
    }
    
    public boolean equals(Object obj){
    	if(obj instanceof Employee){
    		Employee empObj = (Employee)obj;
    		if(firstName == null){
    			if(empObj.firstName !=null){
    				return false;
    			}    				
    		}else if(!firstName.equals(empObj.firstName)){
    			return false;
    		}
    		if(lastName == null){
    			if(empObj.lastName !=null){
    				return false;
    			}    				
    		}else if(!lastName.equals(empObj.lastName)){
    			return false;
    		}
    		if(id != empObj.id){
    			return false;
    		}
    		return true;
    	}
    	return false;
    }

}
