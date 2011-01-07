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
 *     07/31/2008-1.1 Guy Pelletier 
 *       - 241388: JPA cache is not valid after a series of EntityManager operations
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


@Entity
@Table(name="FIELD_CHILD")
public class Child implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Version
    @Column
    private Integer version;
    
    @ManyToOne(cascade = {CascadeType.ALL})
    private Parent parent;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createdOn = new Date();
    
    public Child() {}

    public Integer getId() {
        return this.id;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }
    
    public Parent getParent() {
        return parent;
    }
    
    public int getVersion() {
        return version;
    }
    
    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setParent(Parent parent) {
        this.parent = parent;
    }
}

