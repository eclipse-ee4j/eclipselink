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
 *     Denise Smith - September 2013
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.inheritance.dot;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="a.b.employee")
public class Employee extends Person{
    public String badgeNumber;
 
    public boolean equals(Object obj){
    	if(obj instanceof Employee){
    		return super.equals(obj) && badgeNumber.equals(((Employee)obj).badgeNumber);
    	}
    	return false;
    }
	   
}
