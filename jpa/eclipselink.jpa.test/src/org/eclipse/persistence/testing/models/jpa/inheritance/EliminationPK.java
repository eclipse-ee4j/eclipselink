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
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inheritance;

public class EliminationPK {
    private Integer id;
    private String name;

    public EliminationPK() {}
    
    public EliminationPK(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
       
    public Integer getId() { 
        return id; 
    }

    public String getName() {
        return name;
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (this.getName() != null ? this.getName().hashCode() : 0);
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean equals(Object anotherEliminationPK) {
        if (anotherEliminationPK.getClass() != EliminationPK.class) {
            return false;
        }
            
        return getId().equals(((EliminationPK) anotherEliminationPK).getId()) && 
               getName().equals(((EliminationPK) anotherEliminationPK).getName());
    }
}
