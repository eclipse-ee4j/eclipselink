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
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     08/11/2010-2.2 Guy Pelletier 
 *       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name="CMP3_HEINEKEN")
public class Heineken extends Beer<Integer, Double, Heineken> implements Cloneable {
    public Heineken() {}
    
    public Heineken clone() throws CloneNotSupportedException {
        return (Heineken) super.clone();
    }
    
    public boolean equals(Object anotherHeineken) {
        if (anotherHeineken.getClass() != Heineken.class) {
            return false;
        }
        
        return (getId().equals(((Heineken)anotherHeineken).getId()));
    }

    // preupdate method required for test for bug 299847
    @PreUpdate
    public void preUpdate() {
    }
}

