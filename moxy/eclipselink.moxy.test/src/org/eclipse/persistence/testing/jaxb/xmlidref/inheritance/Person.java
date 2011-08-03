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
 *     Denise Smith - EclipseLink 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

import javax.xml.bind.annotation.XmlID;

public class Person {

	@XmlID
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}
		if(obj instanceof Person){
		    Person personObject = (Person)obj;
		    return id == personObject.getId();		    	
		}
		return false;
	}
}
