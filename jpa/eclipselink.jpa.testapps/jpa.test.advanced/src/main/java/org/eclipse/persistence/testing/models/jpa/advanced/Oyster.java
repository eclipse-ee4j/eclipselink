/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     dminsky - initial implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
