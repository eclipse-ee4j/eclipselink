/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Denise Smith
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.accessors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Address {
	private String id;
    private String city;
    private int streetNumber;
    
   
	public void setStreetNumber(int streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


    public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean equals(Object obj) {
        if(!(obj instanceof Address)) {
            return false;
        }
        Address addr = (Address)obj;
        return addr.id.equals(id) && addr.streetNumber== streetNumber && addr.city.equals(city);
    }
}
