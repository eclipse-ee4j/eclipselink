/*
 * Copyright (c) 2012, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;

import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.SUFFIX;

@Entity
@Table(name="JPA_CANDIDATE")
@Multitenant(TABLE_PER_TENANT)
@TenantTableDiscriminator(type=SUFFIX)
@SecondaryTable(name="JPA_CANDY_SALARY")
public class Candidate {
    @Id
    @GeneratedValue
    public long id;

    @Column(table="JPA_CANDY_SALARY")
    public int salary;

    public String name;

    @ManyToOne
    public Riding riding;

    @ManyToOne
    public Party party;

    @ManyToMany
    @JoinTable(
        name="JPA_CAN_SUP",
        joinColumns=@JoinColumn(name="CANDIDATE_ID"),
        inverseJoinColumns=@JoinColumn(name="SUPPORTER_ID")
    )
    public List<Supporter> supporters;

    @ElementCollection
    @CollectionTable(name="JPA_CANDIDATE_HONORS")
    @Column(name="HONOR")
    public List<String> honors;

    public Candidate() {
        honors = new ArrayList<String>();
        supporters = new ArrayList<Supporter>();
    }

    public void addHonor(String honor) {
        honors.add(honor);
    }

    public void addSupporter(Supporter supporter) {
        supporters.add(supporter);
        supporter.addSupportedCandidate(this);
    }

    public List<String> getHonors() {
        return honors;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Party getParty() {
        return party;
    }

    public Riding getRiding() {
        return riding;
    }

    public int getSalary() {
        return salary;
    }

    public List<Supporter> getSupporters() {
        return supporters;
    }

    public void setHonors(List<String> honors) {
        this.honors = honors;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setRiding(Riding riding) {
        this.riding = riding;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setSupporters(List<Supporter> supporters) {
        this.supporters = supporters;
    }
}
