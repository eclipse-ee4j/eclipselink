/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.model.auction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NamedQueries({
    @NamedQuery(
        name="Bid.all", 
        query="SELECT b FROM StaticBid b"
    ),
    @NamedQuery(
            name="Bid.byId", 
            query="SELECT b FROM StaticBid b WHERE b.id = :id"
    )
})

@Entity
@Table(name = "JPARS_ST_AUC_BID")
public class StaticBid {

    @Id
    @GeneratedValue
    private int id;
    
    private double amount;
    
    private long time;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private StaticUser user;

    @OneToOne(fetch=FetchType.LAZY)
    private StaticAuction auction;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public StaticUser getUser() {
        return user;
    }

    public void setUser(StaticUser user) {
        this.user = user;
    }

    public StaticAuction getAuction() {
        return auction;
    }

    public void setAuction(StaticAuction auction) {
        this.auction = auction;
    }

}
