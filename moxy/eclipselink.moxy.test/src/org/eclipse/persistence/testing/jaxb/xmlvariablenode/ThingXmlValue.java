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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

public class ThingXmlValue {

	@XmlTransient
	public String thingName;
	@XmlValue
	public String thingValue;
		
	public String getThingName() {
		return thingName;
	}

	@XmlTransient
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ThingXmlValue){
		    return ((thingName == null && ((ThingXmlValue)obj).thingName ==null) || thingName.equals(((ThingXmlValue)obj).thingName)) && 	
		    thingValue.equals(((ThingXmlValue)obj).thingValue);
		}
		return false;
	}
}
