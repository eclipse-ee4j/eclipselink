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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * This model tests Example #3 of the mapsId cases.
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_PRIVATE")
public class Private {
    @EmbeddedId
    @AttributeOverride(name="name", column=@Column(name="PRIVATE_NAME"))
    PrivateId privateId;

    @ManyToOne
    // Default the join columns. Since it is to a composite primary key
    // the default pks's are the same as the pk's of the referenced entity.
    @MapsId("corporalPK")
    Corporal corporal;

    public PrivateId getId() {
        return privateId;
    }

    public Corporal getCorporal() {
        return corporal;
    }

    public void setId(PrivateId id) {
        this.privateId = id;
    }

    public void setCorporal(Corporal corporal) {
        this.corporal = corporal;
    }
}
