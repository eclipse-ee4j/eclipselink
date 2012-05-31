/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     31/05/2012-2.4 Guy Pelletier  
 *       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;

@Entity
@Table(name="GEN_RIDING")
public class Riding {
    @Id
    @GeneratedValue
    public Integer id;
    public String name;
    
    public Riding() {}
    
    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
