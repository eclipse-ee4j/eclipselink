/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
