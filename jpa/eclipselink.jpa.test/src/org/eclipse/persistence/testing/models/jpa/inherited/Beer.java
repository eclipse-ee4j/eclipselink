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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.PostPersist;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.ExistenceChecking;

import static javax.persistence.FetchType.LAZY;
import static org.eclipse.persistence.annotations.ExistenceType.CHECK_CACHE;

import java.sql.Timestamp;

@MappedSuperclass
@ExistenceChecking(CHECK_CACHE)
public class Beer<G, H> extends Beverage<G> {
    private Timestamp version;
    private H alcoholContent;
    private BeerConsumer beerConsumer;
    
    public static int BEER_PRE_PERSIST_COUNT = 0;
    public static int BEER_POST_PERSIST_COUNT = 0;
    
    public Beer() {}
    
    @PrePersist
    public void celebrate() {
        BEER_PRE_PERSIST_COUNT++;
    }
    
    @PostPersist
    public void celebrateSomeMore() {
        BEER_POST_PERSIST_COUNT++;
    }
    
    public Object clone() throws CloneNotSupportedException {
        Beer beer = (Beer)super.clone();
        beer.setBeerConsumer(null);
        return beer;
    }
    
    @Basic
    @Column(name="ALCOHOL_CONTENT")
    public H getAlcoholContent() {
        return alcoholContent;
    }
    
    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="C_ID")
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }
    
    @Version
    public Timestamp getVersion() {
        return version;
    }
    
    public void setAlcoholContent(H alcoholContent) {
        this.alcoholContent = alcoholContent;
    }
    
    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }
    
    public void setVersion(Timestamp version) {
        this.version = version;
    }
}
