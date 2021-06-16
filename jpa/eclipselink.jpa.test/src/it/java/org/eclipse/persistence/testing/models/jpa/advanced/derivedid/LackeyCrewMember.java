/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Jun 17, 2009-2.0 Chris Delahunt
//       - Bug#280350: NoSuchFieldException on deploy when using parent's compound PK class as derived ID
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * @author Chris Delahunt
 *
 */

@Entity
@Table(name="JPA_LACKEYCREW")
@IdClass(LackeyCrewMemberId.class)
public class LackeyCrewMember {

    @OneToOne
    @Id
    Lackey lackey;

    @Id
    @Column(name = "JPA_LACKEYCREW_RANK")
    int rank;

    public Lackey getLackey() {
        return lackey;
    }

    public void setLackey(Lackey lackey) {
        this.lackey = lackey;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
