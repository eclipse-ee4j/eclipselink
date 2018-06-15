/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpars.test.model.auction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "JPARS_ST_AUC_ADDRESS")
@IdClass(org.eclipse.persistence.jpars.test.model.auction.StaticAddressId.class)
public class StaticAddress {

    @Id
    @GeneratedValue
    protected int id;
    @Id
    protected String type;
    protected String city;
    protected String street;
    protected String postalCode;
    @OneToOne(mappedBy="address", fetch=FetchType.LAZY)
    protected StaticUser user;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public StaticUser getUser() {
        return user;
    }
    public void setUser(StaticUser user) {
        this.user = user;
    }

}
