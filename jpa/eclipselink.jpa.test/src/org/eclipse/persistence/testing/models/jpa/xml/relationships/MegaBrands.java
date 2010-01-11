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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema   
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.relationships;

public class MegaBrands implements Distributor {
    private Integer distributorId;
    private String name;
    
    public MegaBrands() {}
    
    public Integer getDistributorId() { 
        return distributorId; 
    }
    
    public String getName() {
        return name;
    }
    
    public void setDistributorId(Integer distributorId) { 
        this.distributorId = distributorId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
