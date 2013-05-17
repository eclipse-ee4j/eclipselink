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
 *     Denise Smith - May 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

public class Thing {

	@XmlElement	
	public String thingName;
	public String thingValue;
	
	public Integer thingInt;
	
	@XmlTransient
	public QName thingQName;
		
	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Thing){
		    return ((thingInt == null && ((Thing)obj).thingInt == null) || (thingInt.equals(((Thing)obj).thingInt)))&&
		    thingName.equals(((Thing)obj).thingName) && 	
		    thingValue.equals(((Thing)obj).thingValue) &&
		    ((thingQName == null && ((Thing)obj).thingQName ==null)|| (thingQName.equals(((Thing)obj).thingQName)));
		}
		return false;
	}
}
