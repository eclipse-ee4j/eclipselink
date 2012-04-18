/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpars.test.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    )
    ,
    @NamedQuery(
        name="User.updateName", 
        query="UPDATE StaticUser u SET u.name = :name where u.id = :id"
    )
})

@Entity
@Table(name="ST_AUC_USER")
public class StaticUser {
    
    @Id
    @GeneratedValue
    private int id;

    private String name;
    
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name="ADDRESS_ID", referencedColumnName="ID"),
        @JoinColumn(name="ADDRESS_TYPE", referencedColumnName="TYPE")
    })
    private StaticAddress address;

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
