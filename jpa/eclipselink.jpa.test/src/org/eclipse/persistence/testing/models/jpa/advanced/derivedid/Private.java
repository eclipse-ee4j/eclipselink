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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

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
