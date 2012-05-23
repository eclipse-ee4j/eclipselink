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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlattribute;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee-data")
public class EmployeeNoNamespace 
{
	@XmlAttribute(name="id")
	public int id;

	@XmlAttribute
	public static final String country = "CANADA"; 

	public String toString()
	{
		return "EMPLOYEE: " + id + " " + country;
	}

	public boolean equals(Object object) {
		EmployeeNoNamespace emp = ((EmployeeNoNamespace)object);
		if(emp.id != this.id){
			return false;
		}
		if(!emp.country.equals(this.country)){
			return false;
		}
		return true;
	}
}
