/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="TPC_ELIMINATION")
@Inheritance(strategy=TABLE_PER_CLASS)
@IdClass(EliminationPK.class)
public class Elimination {
    @Id
    private Integer id;
 
    @Id
    private String name;
 
    @Column(name="DESCRIP")
    private String description;
    
    @ManyToOne(cascade=PERSIST)
    @JoinColumn(name="ASSASSIN_ID")
    private Assassin assassin;

    public Elimination () {}
    
    public Assassin getAssassin() {
        return assassin;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isDirectElimination() {
        return false;
    }
    
    public boolean isElimination() {
        return true;
    }
    
    public boolean isIndirectElimination() {
        return false;
    }
    
    public EliminationPK getPK() {
        return new EliminationPK(id, name);
    }

    public void setAssassin(Assassin assassin) {
        this.assassin = assassin;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
