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
 *     Denise Smith - 2.3
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.eclipse.persistence.platform.xml.XMLComparer;

@XmlRootElement(name="employee")
@XmlType(name="employee")
public class EmployeeArray {
	@XmlAttribute
	public String name;
	
	@XmlElement(name="home-address")
	public Address homeAddress;
	
	@XmlAnyElement
	public Object[] elements;
	
	public boolean equals(Object obj) {
		if(!(obj instanceof EmployeeArray)) {
			return false;
		}
		
		EmployeeArray emp = (EmployeeArray)obj;
		if(!(name.equals(emp.name))) {
			return false;
		}
		if(!(homeAddress.equals(emp.homeAddress))) {
			return false;
		}
		if(!(elements.length == emp.elements.length)) {
			return false;
		}
		
		XMLComparer comparer = new XMLComparer();
		
		for(int i=0; i< elements.length; i++){
			Object next1 = elements[i];
			Object next2 = emp.elements[i];
			
			if((next1 instanceof org.w3c.dom.Element) && (next2 instanceof Element)) {
				Element nextElem1 = (Element)next1;
				Element nextElem2 = (Element)next2;
				if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
					return false;
				}
			} else {
				if(!(next1.equals(next2))) {
					return false;
				}
			}
		}
		return true;
	}

}
