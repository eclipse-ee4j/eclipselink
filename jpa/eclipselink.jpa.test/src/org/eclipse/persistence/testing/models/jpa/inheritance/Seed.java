/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_SEED")
public class Seed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    
    @Basic
    @Column(length=64, nullable=false)
    protected String name;
    
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    protected SeededFruit seededFruit;
    
    public Seed() {
        super();
        setName(String.valueOf(UUID.randomUUID()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SeededFruit getSeededFruit() {
        return seededFruit;
    }

    public void setSeededFruit(SeededFruit seededFruit) {
        this.seededFruit = seededFruit;
    }
    
}
