/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/16/2010-2.2 Guy Pelletier 
 *       - 260296: mixed access with no Transient annotation does not result in error
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA_BUILDING")
@Access(AccessType.PROPERTY)
public class BuildingBylaw extends Bylaw {
    @Access(AccessType.FIELD)
    @Column(name="DESCRIP")
    public String description;

    protected Bylaw relatedByLaw;
    protected CityNumberPair reference; 

    // The access type is FIELD. If we map this map instead of its associated
    // methods marked as access PROPERTY, we will map the wrong column name
    // and tests from InheritedModelJunitTest.
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch=FetchType.LAZY, targetEntity=BuildingBylaw.class)
    @JoinColumns({
        @JoinColumn(name="RELATED_ID", referencedColumnName="NUMB"), 
        @JoinColumn(name="RELATED_CITY", referencedColumnName="CITY")
    })
    public Bylaw getRelatedByLaw() {
        return relatedByLaw;
    }

    public void setRelatedByLaw(Bylaw relatedByLaw) {
        this.relatedByLaw = relatedByLaw;
    }

    @ManyToOne(fetch=FetchType.LAZY, targetEntity=BuildingBylaw.class)
    @JoinColumns({
        @JoinColumn(name="REF_ID", referencedColumnName="NUMB"), 
        @JoinColumn(name="REF_CITY", referencedColumnName="CITY")
    })
    public CityNumberPair getReference() {
        return reference;
    }

    public void setReference(CityNumberPair reference) {
        this.reference = reference;
    }
}
