/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltransient;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="address")
public class Address {

    @XmlTransient
    public String name;
    
    public String getName(){
    	return name;
    }
   
    public void setName(String name){
    	this.name = name;
    }
    
    @XmlElement(name="street")
    public String street;
    
    @XmlElement(name="city")
	public String city;
    
    @XmlElement(name="country")
	public String country;

	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Address)) {
			return false;
		}
		Address tgtAddress = (Address) obj;
		return (tgtAddress.city.equals(city) &&
				tgtAddress.country.equals(country) &&
				tgtAddress.name.equals(name) &&
				tgtAddress.street.equals(street) );
	}
}
