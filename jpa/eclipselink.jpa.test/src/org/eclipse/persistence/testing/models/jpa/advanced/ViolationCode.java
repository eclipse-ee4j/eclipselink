/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

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
