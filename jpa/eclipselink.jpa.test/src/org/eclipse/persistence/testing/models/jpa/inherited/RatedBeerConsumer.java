/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-1.1 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)   
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Access;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import static javax.persistence.AccessType.FIELD;
import static javax.persistence.AccessType.PROPERTY;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;

@MappedSuperclass
@Access(FIELD)
public abstract class RatedBeerConsumer<X, Y, Z> extends BeerConsumer {
    @BasicCollection(valueColumn=@Column(name="ACCLAIM"))
    private Collection<X> acclaims;
    
    // Let the key column default. Should default to AWARDS_KEY
    // A keyColumn specification is tested in org.eclipse.persistence.testing.models.jpa.advanced.Buyer
    @BasicMap(valueColumn=@Column(name="AWARD_CODE"))
    private Map<Y, Z> awards;
    
    // An element collection representing a direct collection mapping.
    @ElementCollection
    @Column(name="DESIGNATION")
    // CollectionTable will default in this case, Entity name + "_" + attribute name
    // JoinColumns will default in this case which are different from BasicCollection collection table default
    private Collection<String> designations;
    
    // An element collection representing an aggregate collection mapping.
    @ElementCollection
    // CollectionTable will default
    // Column is not applicable here.
    // JoinColumns will default.
    // Both expert and novice consumers will define attribute overrides and
    // association overrides to apply to their respective tables.
    private Collection<Record> records;
    
    @Transient
    private int iq;
    
    protected RatedBeerConsumer() {
        super();
        acclaims = new Vector<X>();
        awards = new Hashtable<Y, Z>();
        designations = new ArrayList<String>();
        records = new ArrayList<Record>();
    }
    
    public Collection<X> getAcclaims() {
        return acclaims;
    }
    
    public Map<Y, Z> getAwards() {
        return awards;
    }

    public Collection<String> getDesignations() {
        return designations;
    }
    
    @Basic
    @Column(name="CONSUMER_IQ")
    @Access(PROPERTY)
    public int getIQ() {
        return iq;
    }
    
    public Collection<Record> getRecords() {
        return records;
    }
    
    public void setAcclaims(Collection<X> acclaims) {
        this.acclaims = acclaims;
    }
    
    public void setAwards(Map<Y, Z> awards) {
        this.awards = awards;
    }

    public void setDesignations(Collection<String> designations) {
        this.designations = designations;
    }
    
    public void setIQ(int iq) {
        this.iq = iq;
    }

    public void setRecords(Collection<Record> records) {
        this.records = records;
    }
}
