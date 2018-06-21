/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This model tests Example #4 of the mapsId cases.
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_LIEUTENANT_GENERAL")
public class LieutenantGeneral {
    @Id
    Integer id;

    @OneToOne
    // join columns will default as general_general_id
    @MapsId
    General general;

    public General getGeneral() {
        return general;
    }

    public Integer getId() {
        return id;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
