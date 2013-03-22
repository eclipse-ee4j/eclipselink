/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle, Daryl Davis. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/10/2008-2.4.1 Daryl Davis
 *       - 386939: @ManyToMany Map<Entity,Entity> unidirectional reverses Key and Value fields on Update 
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="DETAIL_ID")
    public String id;
    public String name;
    
    public DetailEntity() {
    }
    
    public DetailEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String toString() {
        return "Detail Entity [" + id + ", " + name + "]"; 
    }
}
