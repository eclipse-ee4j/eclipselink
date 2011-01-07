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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

public class MajorId {
    public String firstName;
    public String lastName;
    
    public MajorId() {}
    
    public MajorId(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean equals(Object other) {
        if (other instanceof MajorId) {
            final MajorId otherMajorId = (MajorId) other;
            return (otherMajorId.firstName.equals(firstName) && otherMajorId.lastName.equals(lastName));
        }
        
        return false;
    }
}
