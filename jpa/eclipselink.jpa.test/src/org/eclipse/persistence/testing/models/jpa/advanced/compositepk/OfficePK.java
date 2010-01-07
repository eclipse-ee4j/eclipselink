/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 280436
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

public class OfficePK {

    protected int id;
    protected String location;
    
    public OfficePK(int id, String location) {
        this.id = id;
        this.location = location;
    }

    public boolean equals(Object other) {
        if (other instanceof OfficePK) {
            final OfficePK otherOfficePK = (OfficePK) other;
            return (otherOfficePK.id == id && otherOfficePK.location.equals(location));
        }
        
        return false;
    }
    
}
