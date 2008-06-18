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
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;

@MappedSuperclass
public abstract class RatedBeerConsumer<X, Y, Z> extends BeerConsumer {
    private Collection<X> acclaims;
    private Map<Y, Z> awards;
    
    protected RatedBeerConsumer() {
        super();
        acclaims = new Vector<X>();
        awards = new Hashtable<Y, Z>();
    }
    
    @BasicCollection(valueColumn=@Column(name="ACCLAIM"))
    public Collection<X> getAcclaims() {
        return acclaims;
    }
    
    @BasicMap(keyColumn=@Column(name="AWARD_KEY"), valueColumn=@Column(name="AWARD_CODE"))
    public Map<Y, Z> getAwards() {
        return awards;
    }
    
    public void setAcclaims(Collection<X> acclaims) {
        this.acclaims = acclaims;
    }
    
    public void setAwards(Map<Y, Z> awards) {
        this.awards = awards;
    }
}
