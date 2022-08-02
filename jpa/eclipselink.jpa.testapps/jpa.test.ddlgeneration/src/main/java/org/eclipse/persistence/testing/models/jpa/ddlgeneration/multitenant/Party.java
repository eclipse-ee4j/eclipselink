/*
 * Copyright (c) 2012, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="GEN_PARTY")
public class Party {
    @Id
    @GeneratedValue
    public Integer id;

    //@OneToMany(mappedBy="party")
    //public List<Candidate> candidates;

    public String name;

    public Party() {
        //candidates = new ArrayList<Candidate>();
    }

    public void addCandidate(Candidate candidate) {
        //candidates.add(candidate);
        candidate.setParty(this);
    }

    //public List<Candidate> getCandidates() {
      //  return candidates;
    //}

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //public void setCandidates(List<Candidate> candidates) {
      //  this.candidates = candidates;
    //}

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
