/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import javax.xml.bind.annotation.*;

@XmlRootElement(namespace="my/namespace/is/cool", name="employee-data")
public class EmployeeNamespace 
{
	@XmlAttribute(name="id")
	public int id;


	public String toString()
	{
		return "EMPLOYEE: " + id;
	}

	public boolean equals(Object object) {
		EmployeeNamespace emp = ((EmployeeNamespace)object);
		return emp.id == this.id;
	}
}
