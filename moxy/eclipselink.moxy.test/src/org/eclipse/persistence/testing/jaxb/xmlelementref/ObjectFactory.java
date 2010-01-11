/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import javax.xml.bind.annotation.*;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
@XmlRegistry
public class ObjectFactory {

	@XmlElementDecl(name="root")
	public JAXBElement<String> createRoot() {
		return new JAXBElement<String>(new QName("root"), String.class, "");
	}
	
	@XmlElementDecl(namespace="myns", name="integer-root")
	public JAXBElement<Integer> createIntegerRoot() {
		return new JAXBElement<Integer>(new QName("myns", "integer-root"), Integer.class, new Integer(0));
	}
	
	public EmployeeSingle createEmployeeSingle() {
		return new EmployeeSingle();
	}
	
	public EmployeeCollection createEmployeeCollection() {
		return new EmployeeCollection();
	}
}
