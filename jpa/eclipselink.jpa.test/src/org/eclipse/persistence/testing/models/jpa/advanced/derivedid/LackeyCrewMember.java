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
//     Jun 17, 2009-2.0 Chris Delahunt
//       - Bug#280350: NoSuchFieldException on deploy when using parent's compound PK class as derived ID
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
