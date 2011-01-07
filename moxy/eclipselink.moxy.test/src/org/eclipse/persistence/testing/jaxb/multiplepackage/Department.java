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
 *     Denise Smith - September 3, 2009 Initial test
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.multiplepackage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmlelement.EmployeeNamespace;

@XmlRootElement(name="dept", namespace="somenamespace")
public class Department 
{
	@XmlElement(name="id")
	public int id;
	
	public EmployeeNamespace emp;

	public String toString()
	{
		return "Dept: " + id;
	}

	public boolean equals(Object object) {
		Department dept = ((Department)object);
		if(dept.id != this.id){
			return false;
		}
		if(!emp.equals(dept.emp)){
			return false;
		}
		return true;
	}

}
