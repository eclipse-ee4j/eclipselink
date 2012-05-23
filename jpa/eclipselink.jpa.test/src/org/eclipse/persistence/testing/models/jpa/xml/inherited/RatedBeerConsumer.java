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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)   
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Transient;

public abstract class RatedBeerConsumer<X, Y, Z> extends BeerConsumer {
    private Collection<X> acclaims;
    private Map<Y, Z> awards;
    private List<String> designations;
    private Collection<Record> records;
    private Accredidation accredidation;
    @Transient private int iq;
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

    public int getIQ() {
        return iq;
    }
    
    public Collection<Record> getRecords() {
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

    public void setRecords(Collection<Record> records) {
        this.records = records;
    }
}
