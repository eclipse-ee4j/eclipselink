/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl;

public class Utensil {
    public Integer id;
    public String serialTag;
    
    public Utensil() {}
    
    public Integer getId() {
        return id;
    }
    
    public String getSerialTag() {
        return serialTag;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setSerialTag(String serialTag) {
        this.serialTag = serialTag;
    }
}
