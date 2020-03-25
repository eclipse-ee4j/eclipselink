/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.Collection;
import java.util.Vector;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import org.eclipse.persistence.annotations.Multitenant;

import static jakarta.persistence.CascadeType.ALL;

public class MafiaFamily implements Serializable {
    private int id;
    private String name;
    private Collection<Mafioso> mafiosos;
    private Collection<String> tags;
    private Double revenue;

    public MafiaFamily() {
        this.tags = new Vector<String>();
        this.mafiosos = new Vector<Mafioso>();
    }

    public void addMafioso(Mafioso mafioso) {
        mafiosos.add(mafioso);
        mafioso.setFamily(this);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public int getId() {
        return id;
    }

    public Collection<Mafioso> getMafiosos() {
        return mafiosos;
    }

    public String getName() {
        return name;
    }
    public Collection<String> getTags() {
        return tags;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMafiosos(Collection<Mafioso> mafiosos) {
        this.mafiosos = mafiosos;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }
}
