/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/06/2011-2.3 Guy Pelletier 
 *       - 371453: JPA Multi-Tenancy in Bidirectional OneToOne Relation throws ArrayIndexOutOfBoundsException
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

@Entity
@Table(name="JPA_ENVELOPE")
@Multitenant(SINGLE_TABLE)
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id")
public class Envelope {
    @Id
    @GeneratedValue
    @Column(name="ID")
    public int id;
    
    @Column(name="COLOR")
    public String color;
    
    @OneToOne(mappedBy="envelope", cascade={CascadeType.ALL})
    public Card card;

    public Card getCard() {
        return card;
    }
    
    public String getColor() {
        return color;
    }
    
    public int getId() {
        return id;
    }
    
    public void setCard(Card card) {
        this.card = card;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
