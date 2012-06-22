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
 * mmacivor - December 15/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="employee", namespace="someUri")
public class Employee {

    public String firstName;
    public String lastName;
    
    public boolean equals(Object theObject){
    	if(theObject instanceof Employee){
    		Employee emp = (Employee)theObject;
    	   if(!firstName.equals(emp.firstName)){
    		   return false;
    	   }
    	   if(!lastName.equals(emp.lastName)){
    		   return false;
    	   }
    	   return true;
    	}else{
    	   return false;
    	}
    }
}
