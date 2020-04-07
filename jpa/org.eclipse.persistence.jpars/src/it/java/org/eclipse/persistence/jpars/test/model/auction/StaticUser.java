/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpars.test.model.auction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.eclipse.persistence.annotations.PrivateOwned;

@NamedQueries({
    @NamedQuery(
        name="User.all",
        query="SELECT u FROM StaticUser u"
    ),
    @NamedQuery(
            name="User.byId",
            query="SELECT u FROM StaticUser u where u.id = :id"
    ),
    @NamedQuery(
        name="User.byName",
        query="SELECT u FROM StaticUser u where u.name = :name"
    ),
    @NamedQuery(
        name="User.byNameOrId",
        query="SELECT u FROM StaticUser u where u.name = :name or u.id = :id"
    ),
    @NamedQuery(
        name="User.updateName",
        query="UPDATE StaticUser u SET u.name = :name where u.id = :id"
    ),
    @NamedQuery(
            name="User.nameAndId",
            query="SELECT u.name, u.id FROM StaticUser u"
    ),
    @NamedQuery(
            name="User.count",
            query="SELECT count(u) FROM StaticUser u"
    )
})

@NamedNativeQueries({
    @NamedNativeQuery(
        name="User.nativeName",
        query="SELECT ID, NAME, ADDRESS_ID, ADDRESS_TYPE FROM ST_AUC_USER"
    )
})

@Entity
@Table(name = "JPARS_ST_AUC_USER")
public class StaticUser {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="ADDRESS_ID", referencedColumnName="ID"),
        @JoinColumn(name="ADDRESS_TYPE", referencedColumnName="TYPE")
    })
    @PrivateOwned
    private StaticAddress address;

    @Version
    private int version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StaticAddress getAddress() {
        return address;
    }

    public void setAddress(StaticAddress address) {
        this.address = address;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean equals(Object object){
        if (object == null || !(object instanceof StaticUser)){
            return false;
        }
        StaticUser user = (StaticUser)object;
        if (address == null && user.getAddress() != null){
            return false;
        }
        if (name == null && user.getName() != null){
            return false;
        }
        return id == user.getId() && name.equals(user.getName()) && (address == null || address.getId() == user.getAddress().getId());
    }


}
