/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlaccessortype;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="address")
public class Address {
	
	private int id;
	
	@XmlAttribute
	public String lastName;
	@XmlElement
	public String street;
	@XmlElement
	public String city;
	public String country;
	
	
	public int getID(){
		return id;
	}
	
	public void setID(int i){
		id = i;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Address)) {
			return false;
		}
		Address addr = (Address)obj;
		return (addr.id == id );//&& addr.street.equals(street) && addr.city.equals(city) && addr.country.equals(country));
	}

}
