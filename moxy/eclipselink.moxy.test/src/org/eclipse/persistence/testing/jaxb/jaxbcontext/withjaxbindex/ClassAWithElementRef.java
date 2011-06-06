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
 * Denise Smith - 2.3
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
public class ClassAWithElementRef {
	private Object theValue;
	public List<String> theValues;

	public ClassAWithElementRef(){
		theValues = new ArrayList<String>();
	}
	
	public Object getTheValue() {
		return theValue;
	}

	@XmlElementRefs({
	        @XmlElementRef(name="a") })
	 @XmlMixed
	public void setTheValue(Object theValue) {
		this.theValue = theValue;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof ClassAWithElementRef)){
			return false;
		}
		ClassAWithElementRef classAObj = (ClassAWithElementRef)obj;
		
		if(theValue == null){
			if(classAObj.getTheValue() != null){
				return false;
			}
		}else{
			if(classAObj.getTheValue() == null){
				return false;
			}
			if(getTheValue() instanceof JAXBElement){
				if(!(classAObj.getTheValue() instanceof JAXBElement)){
					return false;
				}else{
					JAXBElement jb1 = (JAXBElement)getTheValue();
					JAXBElement jb2 = (JAXBElement)classAObj.getTheValue();
					if(!jb1.getValue().equals(jb2.getValue())){
						return false;
					}
					if(!jb1.getName().equals(jb2.getName())){
						return false;
					}
				}
			}else if(!getTheValue().equals(classAObj.getTheValue())){
				return false;
			}
		}		
		
		return true;
	}
}