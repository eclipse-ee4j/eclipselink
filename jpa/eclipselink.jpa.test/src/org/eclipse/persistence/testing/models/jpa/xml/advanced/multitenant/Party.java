/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.ArrayList;
import java.util.List;

public class Party {
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
