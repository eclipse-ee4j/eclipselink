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
 *     04/02/2008-1.0M6 Guy Pelletier 
 *       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.util.List;  

public class OwnershipDetails {
    private Integer id;
    private Integer version;
    private List<String> privileges;
    private String transientValue;
    
    public Integer getId() {
        return id;
    }
    
    public List<String> getPrivileges() {
        return privileges;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public String getTransientValue() {
        return transientValue;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public void setTransientValue(String transientValue) {
        this.transientValue = transientValue;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
}
