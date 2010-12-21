/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

@Embeddable
public class Accredidation {
    @Column(name="BOGUS_COLUMN_NAME")
    // Expert beer consumer will use the attribute override from the 
    // accredidation mapping from RatedBeerConsumer, whereas, novice beer
    // consumer will provide a class level attribute override to override
    // the above mentioned attribute override.
    // If these overrides are not picked up, the column name will be picked up
    // and errors will occur.
    private String details;
    
    @OneToMany(cascade={PERSIST, MERGE})
    @JoinColumn(name="BOGUS_JOIN_COLUMN_NAME")
    // Expert beer consumer will use the association overrides from the 
    // accredidation mapping from RatedBeerConsumer, whereas, novice beer
    // consumer will provide a class level association override to override
    // the above mentioned association override.
    // If these overrides are not picked up, this join column will be picked up
    // and errors will occur.
    private List<Official> officials;
    
    @ManyToMany(cascade={PERSIST, MERGE})
    @JoinTable(name="BOGUS_JOIN_TABLE_NAME")
    // Expert beer consumer will use the association overrides from the 
    // accredidation mapping from RatedBeerConsumer, whereas, novice beer
    // consumer will provide a class level association override to override
    // the above mentioned association override.
    // If these overrides are not picked up, the join table will default
    // and errors will occur.
    private List<Witness> witnesses;
    
    public Accredidation() {
        officials = new ArrayList<Official>();
        witnesses = new ArrayList<Witness>();
    }
    
    public void addOfficial(Official official) {
        officials.add(official);
    }
    
    public void addWitness(Witness witness) {
        witnesses.add(witness);
    }
    
    public String getDetails() {
        return details;
    }
    
    public List<Official> getOfficials() {
        return officials;
    }
    
    public List<Witness> getWitnesses() {
        return witnesses;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public void setOfficials(List<Official> officials) {
        this.officials = officials;
    }
    
    public void setWitnesses(List<Witness> witnesses) {
        this.witnesses = witnesses;
    }
}
