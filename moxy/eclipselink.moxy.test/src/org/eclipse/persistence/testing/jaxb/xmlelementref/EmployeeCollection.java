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
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBElement;
import java.util.Iterator;

@XmlRootElement(name="employee-collection")
public class EmployeeCollection {
	
	@XmlElementRefs({@XmlElementRef(name="integer-root", namespace="myns"), @XmlElementRef(name="root"), @XmlElementRef(name="employee-collection")})
	public java.util.List refs;
	
	public boolean equals(Object emp) {
		if(this.refs.size() != ((EmployeeCollection)emp).refs.size()) {
			return false;
		}
		Iterator iter1 = this.refs.iterator();
		Iterator iter2 = ((EmployeeCollection)emp).refs.iterator();
		while(iter1.hasNext()) {
			Object next1 = iter1.next();
			Object next2 = iter2.next();
			if((next1 instanceof JAXBElement) && (next2 instanceof JAXBElement)) {
				JAXBElement elem1 = (JAXBElement)next1;
				JAXBElement elem2 = (JAXBElement)next2;
				if(!(elem1.getName().equals(elem2.getName()) && elem1.getValue().equals(elem2.getValue()) && elem1.getDeclaredType() == elem2.getDeclaredType())) {
					return false;
				}
			} else if(!(next1.equals(next2))) {
				return false;
			}
		}
		
		return true;
	}
}
