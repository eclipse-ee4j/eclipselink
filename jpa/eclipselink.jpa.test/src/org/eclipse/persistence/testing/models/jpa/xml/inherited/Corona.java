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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class Corona extends Beer implements Cloneable {
    public Corona() {}
    
    public Corona clone() throws CloneNotSupportedException {
        return (Corona) super.clone();
    }
    
    public boolean equals(Object anotherCorona) {
        if (anotherCorona.getClass() != Corona.class) {
            return false;
        }
        
        return (getId().equals(((Corona)anotherCorona).getId()));
    }
}
