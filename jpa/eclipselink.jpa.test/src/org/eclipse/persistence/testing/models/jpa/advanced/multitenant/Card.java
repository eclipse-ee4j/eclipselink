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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

@Entity
@Table(name="JPA_CARD")
@Multitenant(SINGLE_TABLE)
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id")
public class Card {
    @Id
    @GeneratedValue
    @Column(name="ID")
    public int id;
    
    @Column(name="COLOR")
    public String color;
    
    @Column(name="OCCASION")
    public String occasion;
    
    @Column(name="FRONT_CAPTION")
    public String frontCaption;
    
    @Column(name="INSIDE_CAPTION")
    public String insideCaption;
    
    @Column(name="PRICE")
    public Double price;
    
    @Column(name="PRINT_YEAR")
    public int printYear;
    
    @OneToOne(optional=false , cascade={CascadeType.ALL})
    @JoinColumn(name="ENVELOPE_ID", unique=true, nullable=false, updatable=false,  referencedColumnName="ID")
    public Envelope envelope;
    
    public String getColor() {
        return color;
    }
    
    public Envelope getEnvelope() {
        return envelope;
    }
    
    public String getFrontCaption() {
        return frontCaption;
    }
    
    public int getId() {
        return id;
    }
    
    public String getInsideCaption() {
        return insideCaption;
    }
    
    public String getOccasion() {
        return occasion;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public int getPrintYear() {
        return printYear;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }
    
    public void setFrontCaption(String frontCaption) {
        this.frontCaption = frontCaption;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setInsideCaption(String insideCaption) {
        this.insideCaption = insideCaption;
    }
    
    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public void setPrintYear(int printYear) {
        this.printYear = printYear;
    }
}
