/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1
//     04/05/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 3)
//     06/1/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 9)
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

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
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumns;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name="JPA_MAFIA_FAMILY")
@SecondaryTable(
    name="JPA_FAMILY_REVENUE",
    pkJoinColumns={@PrimaryKeyJoinColumn(name="FAMILY_ID", referencedColumnName="ID")}
)
@Multitenant
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id", primaryKey=true)
@NamedQueries({
    @NamedQuery(
     name="findAllMafiaFamilies",
     query="SELECT s from MafiaFamily s"),
    @NamedQuery(
     name="DeleteAllMafiaFamilies",
     query="DELETE FROM MafiaFamily m")
})
@NamedNativeQuery(
     name="findSQLMafiaFamilies",
     query="SELECT * FROM JPA_MAFIA_FAMILY")
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

    @Id
    @GeneratedValue
    @Column(name="ID")
    public int getId() {
        return id;
    }

    @OneToMany(cascade=ALL, mappedBy="family")
    public Collection<Mafioso> getMafiosos() {
        return mafiosos;
    }

    @Basic
    public String getName() {
        return name;
    }

    @ElementCollection
    @CollectionTable(
      name="JPA_FAMILY_TAGS",
      joinColumns={
          @JoinColumn(name="FAMILY_ID", referencedColumnName="ID")
      })
    @Column(name="TAG")
    public Collection<String> getTags() {
        return tags;
    }

    @Basic
    @Column(name="REVENUE", table="JPA_FAMILY_REVENUE")
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

    public String toString() {
        return "MafiaFamily[" + getId() + "] : " + name;
    }
}
