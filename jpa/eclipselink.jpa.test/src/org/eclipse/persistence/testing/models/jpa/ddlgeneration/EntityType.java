/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/30/2010-2.3 Guy Pelletier submitted for Paul Fullbright  
 *       - 312253: Descriptor exception with Embeddable on DDL gen
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.AccessType.PROPERTY;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Access(PROPERTY)
public class EntityType {
    
    private String id;
    
    private List<EmbeddableType> assortment;
    
    @Id
    protected String getId() {
        return this.id;
    }
    
    protected void setId(String id) {
        this.id = id;
    }
        
    @ElementCollection
    protected List<EmbeddableType> getAssortment() {
        return this.assortment;
    }
    
    protected void setAssortment(List<EmbeddableType> assortment) {
        this.assortment = assortment;
    }
}
