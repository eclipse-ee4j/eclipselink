/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Transient;

public abstract class RatedBeerConsumer<X, Y, Z> extends BeerConsumer {
    private Collection<X> acclaims;
    private Map<Y, Z> awards;
    private Collection<String> designations;
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
