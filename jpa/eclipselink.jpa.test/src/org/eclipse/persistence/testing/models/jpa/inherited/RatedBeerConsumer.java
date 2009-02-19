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
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;

@MappedSuperclass
public abstract class RatedBeerConsumer<X, Y, Z> extends BeerConsumer {
    private Collection<X> acclaims;
    
    // Let the key column default. Should default to AWARDS_KEY
    // A keyColumn specification is tested in org.eclipse.persistence.testing.models.jpa.advanced.Buyer
    private Map<Y, Z> awards;
    
    private int iq;
    
    protected RatedBeerConsumer() {
        super();
        acclaims = new Vector<X>();
        awards = new Hashtable<Y, Z>();
    }
    
    @BasicCollection(valueColumn=@Column(name="ACCLAIM"))
    public Collection<X> getAcclaims() {
        return acclaims;
    }
    
    @BasicMap(valueColumn=@Column(name="AWARD_CODE"))
    public Map<Y, Z> getAwards() {
        return awards;
    }

    @Basic
    @Column(name="CONSUMER_IQ")
    public int getIQ() {
        return iq;
    }
    
    public void setAcclaims(Collection<X> acclaims) {
        this.acclaims = acclaims;
    }
    
    public void setAwards(Map<Y, Z> awards) {
        this.awards = awards;
    }

    public void setIQ(int iq) {
        this.iq = iq;
    }
}
