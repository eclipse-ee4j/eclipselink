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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

public class PhoneNumberPK  {
    public Integer id;
    public String type;

    public PhoneNumberPK() {}

    public Integer getId() { 
        return id; 
    }
    
    public String getType() { 
        return type; 
    }
    
    public void setId(Integer id) {
        this.id = id;
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
        
        if (! getType().equals(((PhoneNumberPK) anotherPhoneNumber).getType())) {
            return false;
        }
        
        return (getId().equals(((PhoneNumberPK) anotherPhoneNumber).getId()));
    }
}

