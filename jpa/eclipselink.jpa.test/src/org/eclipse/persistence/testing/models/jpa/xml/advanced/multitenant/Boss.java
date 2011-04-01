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
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

public class Boss extends Mafioso {
    private Underboss underboss;

    public Boss() {}

    public Underboss getUnderboss() { 
        return underboss; 
    }

    public void setUnderboss(Underboss underboss) {
        this.underboss = underboss;
        
        if (underboss != null) {
            underboss.setBoss(this);
        }
    }
}
