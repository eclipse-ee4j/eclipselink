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
package org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="address", namespace="myns")
@XmlType(namespace="myns")
public class Address {
	@XmlAttribute (namespace="myns")   
    public String street;
	@XmlAttribute (namespace="")   
    public String street2;
	@XmlElement (namespace="myns")   
    public String street3;
	@XmlElement (namespace="")   
    public String street4;
	@XmlAttribute 
    public String city;
	
	public boolean equals(Object obj){
		if(obj instanceof Address){
			Address addrObj = (Address)obj;
			if(!street.equals(addrObj.street)){
				return false;
			}
			if(!street2.equals(addrObj.street2)){
				return false;
			}
			if(!street3.equals(addrObj.street3)){
				return false;
			}
			if(!street4.equals(addrObj.street4)){
				return false;
			}
			if(!city.equals(addrObj.city)){
				return false;
			}
			return true;
		}
		return false;
	}
}
