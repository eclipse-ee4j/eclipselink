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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * This model tests Example #2 of the mapsId cases.
 *
 * @author gpelleti
 */
@Entity
@Table(name="JPA_CAPTAIN")
public class Captain {
    @EmbeddedId
    @AttributeOverride(name="name", column=@Column(name="someOtherName"))
    CaptainId id;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="FK1", referencedColumnName="F_NAME"),
        @JoinColumn(name="FK2", referencedColumnName="L_NAME")
    })
    @MapsId // use the default value of major to look up the id field.
    Major major;

    public CaptainId getId() {
        return id;
    }

    public Major getMajor() {
        return major;
    }

    public void setId(CaptainId id) {
        this.id = id;
    }

    public void setMajor(Major major) {
        this.major = major;
    }
}
