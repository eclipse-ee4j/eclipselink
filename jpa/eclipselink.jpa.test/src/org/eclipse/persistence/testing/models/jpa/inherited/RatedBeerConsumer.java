/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     04/09/2012-2.4 Guy Pelletier 
 *       - 374377: OrderBy with ElementCollection doesn't work
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Access;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import static javax.persistence.AccessType.FIELD;
import static javax.persistence.AccessType.PROPERTY;
import static javax.persistence.CascadeType.ALL;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;
import org.eclipse.persistence.annotations.OrderCorrection;

import static org.eclipse.persistence.annotations.OrderCorrectionType.EXCEPTION;

@SuppressWarnings("deprecation")
@MappedSuperclass
@Access(FIELD)
public abstract class RatedBeerConsumer<X, Y, Z> extends BeerConsumer<String> {
    @ElementCollection
    @Column(name="ACCLAIM")
    @OrderBy("ASC")
    private Collection<X> acclaims;
    
    // Let the key column default. Should default to AWARDS_KEY
    // A keyColumn specification is tested in org.eclipse.persistence.testing.models.jpa.advanced.Buyer
    @BasicMap(valueColumn=@Column(name="AWARD_CODE"))
    private Map<Y, Z> awards;
    
    // An element collection representing a direct collection mapping.
    @ElementCollection
    @Column(name="DESIGNATION")
    @OrderColumn(name="ORDER_COLUMN")
    @OrderCorrection(EXCEPTION)
    // CollectionTable will default in this case, Entity name + "_" + attribute name
    // JoinColumns will default in this case which are different from BasicCollection collection table default
    private List<String> designations;
    
    // An element collection representing an aggregate collection mapping.
    @ElementCollection
    // CollectionTable will default
    // Column is not applicable here.
    // JoinColumns will default.
    // Both expert and novice consumers will define attribute overrides and
    // association overrides to apply to their respective tables.
    @AttributeOverride(name="description", column=@Column(name="DESCRIP"))
    @OrderColumn(name="ORDER_COLUMN")
    private List<Record> records;
    
    @Embedded
    // Expert beer consumer will use these overrides, whereas, novice beer 
    // consumer will override them by defining class level overrides.
    @AttributeOverride(name="details", column=@Column(name="ACCREDIDATION"))
    @AssociationOverrides({
        @AssociationOverride(name="witnesses", joinTable=@JoinTable(name="EBC_ACCREDIDATION_WITNESS",
            joinColumns=@JoinColumn(name="EBC_ID", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="WITNESS_ID", referencedColumnName="ID"))),
        @AssociationOverride(name="officials", joinColumns=@JoinColumn(name="FK_EBC_ID"))
    })
    private Accredidation accredidation;
    
    @Transient
    private int iq;
    
    // Expert beer consumer will use the join table as is here, whereas, novice
    // beer consumer will provide an association override.
    @ManyToMany(cascade=ALL)
    @JoinTable(
        name="JPA_CONSUMER_COMMITTEE",
        joinColumns=@JoinColumn(name="CONSUMER_ID", referencedColumnName="ID"),
        inverseJoinColumns=@JoinColumn(name="COMMITTEE_ID", referencedColumnName="ID")
    )
    @OrderColumn(name="ORDER_COLUMN")
    private List<Committee> committees;
    
    protected RatedBeerConsumer() {
        super();
        acclaims = new Vector<X>();
        awards = new Hashtable<Y, Z>();
        designations = new ArrayList<String>();
        records = new ArrayList<Record>();
        committees = new ArrayList<Committee>(); 
    }
    
    public Collection<X> getAcclaims() {
        return acclaims;
    }
    
    public Accredidation getAccredidation() {
        return accredidation;
    }
    
    public Map<Y, Z> getAwards() {
        return awards;
    }

    public List<Committee> getCommittees() {
        return committees;
    }
    
    public List<String> getDesignations() {
        return designations;
    }
    
    @Basic
    @Column(name="CONSUMER_IQ")
    @Access(PROPERTY)
    public int getIQ() {
        return iq;
    }
    
    public List<Record> getRecords() {
        return records;
    }
    
    public void setAcclaims(Collection<X> acclaims) {
        this.acclaims = acclaims;
    }
    
    public void setAccredidation(Accredidation accredidation) {
        this.accredidation = accredidation;
    }
    
    public void setAwards(Map<Y, Z> awards) {
        this.awards = awards;
    }

    public void setCommittees(List<Committee> committees) {
        this.committees = committees;
    }
    
    public void setDesignations(List<String> designations) {
        this.designations = designations;
    }
    
    public void setIQ(int iq) {
        this.iq = iq;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }
}
