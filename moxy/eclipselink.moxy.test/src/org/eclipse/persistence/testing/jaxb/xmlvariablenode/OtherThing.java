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

public class OtherThing {

	@XmlAttribute
	public String otherThingName;
	
	@XmlAttribute
	public String otherThingValue;
	
	@XmlAttribute
	public Integer otherThingInt;
	
	public boolean equals(Object obj){
		if(obj instanceof OtherThing){
		    return ((otherThingInt == null && ((OtherThing)obj).otherThingInt == null) || (otherThingInt.equals(((OtherThing)obj).otherThingInt)))&&
		    otherThingName.equals(((OtherThing)obj).otherThingName) && 	
		    otherThingValue.equals(((OtherThing)obj).otherThingValue);
		}
		return false;
	}
}
