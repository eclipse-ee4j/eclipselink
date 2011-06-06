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
 *     08/28/2008-1.1 Guy Pelletier 
 *       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.io.Serializable;

public class Role implements Serializable {
    private int id;
    private String description;
    
    public Role() {}
    
    public Role(String description) { 
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getId() {
        return id;
    }
    
    public void setDescription(String description) {
        this.description = description;    
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String toString() {
        return "Role: Id [" + id + "], Description [" + description + "]";
    }
}
