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
//     dminsky - initial implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Cacheable(false)
@Table(name="CMP3_OYSTER")
public class Oyster {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected int id;
    
    protected String color;
    
    @OneToOne(cascade=CascadeType.ALL, mappedBy="oyster", fetch=FetchType.EAGER)
    protected Pearl pearl;
    
    public Oyster() {
        super();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Pearl getPearl() {
        return pearl;
    }

    public void setPearl(Pearl pearl) {
        this.pearl = pearl;
    }
    
}
