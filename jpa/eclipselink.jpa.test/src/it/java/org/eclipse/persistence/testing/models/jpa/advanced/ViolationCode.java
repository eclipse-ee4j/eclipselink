/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="VIOLATION_CODE")
public class ViolationCode {
    public enum ViolationCodeId {A, B, C, D}

    @Id
    @Enumerated
    public ViolationCodeId id;

    @Column(name="DESCRIP")
    public String description;

    public String getDescription() {
        return description;
    }

    public ViolationCodeId getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(ViolationCodeId id) {
        this.id = id;
    }
}
