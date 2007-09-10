/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/


package org.eclipse.persistence.testing.models.jpa.xml.advanced;

public class PhoneNumberPK  {
    public Integer id;
	public String type;

    public PhoneNumberPK() {}

	public Integer getId() { 
        return id; 
    }
    
	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() { 
        return type; 
    }
    
	public void setType(String type) {
		this.type = type;
	}
    
    /**
     * equals: Answer true if the ids are equal
     */
    public boolean equals(Object anotherPhoneNumber) {
        if (anotherPhoneNumber.getClass() != PhoneNumberPK.class) {
            return false;
        }
        return (getId().equals(((PhoneNumberPK)anotherPhoneNumber).getId()));
    }
}