/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     07/15/2010-2.2 Guy Pelletier
//       -311395 : Multiple lifecycle callback methods for the same lifecycle event
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
//     10/05/2012-2.4.1 Guy Pelletier
//       - 373092: Exceptions using generics, embedded key and entity inheritance
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.PostPersist;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.ExistenceChecking;

import static javax.persistence.FetchType.LAZY;
import static org.eclipse.persistence.annotations.ExistenceType.CHECK_CACHE;

import java.sql.Timestamp;

@MappedSuperclass
@ExistenceChecking(CHECK_CACHE)
public class Beer<PK, ALCOHOL_CONTENT_TYPE, BeerDoubleType> extends Beverage<BeerDoubleType, PK> {
    private Timestamp version;
    private ALCOHOL_CONTENT_TYPE alcoholContent;
    private BeerConsumer beerConsumer;
    private BeerDoubleType beerDouble;

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
    public ALCOHOL_CONTENT_TYPE getAlcoholContent() {
        return alcoholContent;
    }

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="C_ID")
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }

    @OneToOne(fetch=LAZY)
    //@OneToOne
    @JoinColumn(name="BD_ID")
    public BeerDoubleType getBeerDouble() {
        return beerDouble;
    }

    @Version
    public Timestamp getVersion() {
        return version;
    }

    public void setAlcoholContent(ALCOHOL_CONTENT_TYPE alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }

    public void setBeerDouble(BeerDoubleType beerDouble) {
        this.beerDouble = beerDouble;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }
}
