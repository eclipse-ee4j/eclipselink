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
//     31/05/2012-2.4 Guy Pelletier
//       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.PREFIX;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;

@Entity
@Table(name="GEN_SUPPORTER")
@Multitenant(TABLE_PER_TENANT)
@TenantTableDiscriminator(type=PREFIX)
public class Supporter {
    @Id
    @GeneratedValue
    public long id;
    public String name;

    @ManyToMany(mappedBy="supporters")
    public List<Candidate> supportedCandidates;

    public Supporter() {
        supportedCandidates = new ArrayList<Candidate>();
    }

    protected void addSupportedCandidate(Candidate candidate) {
        supportedCandidates.add(candidate);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Candidate> getSupportedCandidates() {
        return supportedCandidates;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSupportedCandidates(List<Candidate> supportedCandidates) {
        this.supportedCandidates = supportedCandidates;
    }
}
