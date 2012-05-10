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
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Iterator;

@XmlRootElement(name="employee")
public class EmployeeLaxMixed {
	@XmlAttribute
	public String name;
	
	@XmlElement(name="home-address")
	public Address homeAddress;
	
	@XmlAnyElement(lax=true)
	@XmlMixed
	public Collection elements;
	
	public boolean equals(Object obj) {
		if(!(obj instanceof EmployeeLaxMixed)) {
			return false;
		}
		
		EmployeeLaxMixed emp = (EmployeeLaxMixed)obj;
		if(!(name.equals(emp.name))) {
			return false;
		}
		if(!(homeAddress.equals(emp.homeAddress))) {
			return false;
		}
		if((elements == null && emp.elements != null)||((emp.elements == null && elements != null))){
			return false;
		}
		
		if(elements !=null && emp.elements != null){
			if(!(elements.size() == emp.elements.size())) {
				return false;
			}
			
			Iterator elements1 = this.elements.iterator();
			Iterator elements2 = emp.elements.iterator();
			XMLComparer comparer = new XMLComparer();
			
			while(elements1.hasNext()) {
				Object next1 = elements1.next();
				Object next2 = elements2.next();
				
				if((next1 instanceof org.w3c.dom.Element) && (next2 instanceof Element)) {
					Element nextElem1 = (Element)next1;
					Element nextElem2 = (Element)next2;
					if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
						return false;
					}
				} else if (next1 instanceof JAXBElement && next2 instanceof JAXBElement){
					JAXBElement nextElem1 = (JAXBElement)next1;
					JAXBElement nextElem2 = (JAXBElement)next2;
					if(!nextElem1.getName().getLocalPart().equals(nextElem2.getName().getLocalPart())){
						return false;
					}
					if(!nextElem1.getName().getNamespaceURI().equals(nextElem2.getName().getNamespaceURI())){
						return false;
					}
					if(!nextElem1.getValue().equals(nextElem2.getValue())){
						return false;
					}
				} else {
					if(!(next1.equals(next2))) {
						return false;
					}
				}
			}
		}
		return true;
	}	

}
