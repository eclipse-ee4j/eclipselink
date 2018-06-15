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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Customizer;

@Entity
@Cacheable(false)
@Table(name="CMP3_PEARL")
@Customizer(AdvancedHistoryCustomizer.class)
public class Pearl {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected Long id;
    
    protected String name;
    
    @OneToOne
    protected Oyster oyster;
    
    public Pearl() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Oyster getOyster() {
        return oyster;
    }

    public void setOyster(Oyster oyster) {
        this.oyster = oyster;
    }
    
}
