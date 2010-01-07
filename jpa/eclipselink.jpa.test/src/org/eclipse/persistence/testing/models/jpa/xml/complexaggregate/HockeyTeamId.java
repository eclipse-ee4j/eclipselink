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
 *     04/02/2008-1.0M6 Guy Pelletier 
 *       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.io.Serializable;

public class HockeyTeamId implements Serializable {
    private Long id;
    private String description;
    
    public HockeyTeamId() {}
    
    public String getDescription() {
        return description;
    }
    
    public Long getId() { 
        return id; 
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(Long id) {
        this.id = id;
    }   
}
