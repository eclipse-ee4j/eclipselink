/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import static javax.persistence.FetchType.LAZY;

import java.sql.Timestamp;

@MappedSuperclass
public class Beer extends Beverage {
    private Timestamp version;
    private double alcoholContent;
    private BeerConsumer beerConsumer;
    
    public static int BEER_PRE_PERSIST_COUNT = 0;
    
    public Beer() {}
    
    @PrePersist
    public void celebrate() {
        BEER_PRE_PERSIST_COUNT++;
    }
    
    @Basic
    @Column(name="ALCOHOL_CONTENT")
    public double getAlcoholContent() {
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
    
    public void setAlcoholContent(double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }
    
    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }
    
    public void setVersion(Timestamp version) {
        this.version = version;
    }
}
