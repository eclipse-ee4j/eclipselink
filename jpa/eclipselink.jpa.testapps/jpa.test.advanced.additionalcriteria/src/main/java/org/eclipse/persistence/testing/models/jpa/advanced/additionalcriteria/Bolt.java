/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.AdditionalCriteria;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name="JPA_AC_BOLT")
@AdditionalCriteria("this.nut.size = :NUT_SIZE and this.nut.color = :NUT_COLOR order by this.id")
public class Bolt {
    @Id
    @GeneratedValue(generator="AC_BOLT_SEQ")
    @SequenceGenerator(name="AC_BOLT_SEQ", allocationSize=25)
    public Integer id;

    @OneToOne(cascade=ALL)
    @JoinColumn(name="NUT_ID")
    public Nut nut;

    public Integer getId() {
        return id;
    }

    public Nut getNut() {
        return nut;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNut(Nut nut) {
        this.nut = nut;
    }
}
