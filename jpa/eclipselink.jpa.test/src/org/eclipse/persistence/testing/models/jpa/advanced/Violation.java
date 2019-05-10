/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name="VIOLATION")
public class Violation implements Serializable {
    public enum ViolationID {V1, V2, V3, V4}

    @Id
    @Enumerated(STRING)
    private ViolationID id;

    @ManyToMany
    @JoinTable(
            name="VIOLATION_CODES",
            joinColumns=@JoinColumn(name="VIOLATION_ID"),
            inverseJoinColumns=@JoinColumn(name="VIOLATION_CODE_ID")
    )
    private List<ViolationCode> violationCodes;

    public Violation() {
        violationCodes = new ArrayList<ViolationCode>();
    }

    public ViolationID getId() {
        return id;
    }

    public List<ViolationCode> getViolationCodes() {
        return violationCodes;
    }

    public void setId(ViolationID id) {
        this.id = id;
    }

    public void setViolationCodes(List<ViolationCode> violationCodes) {
        this.violationCodes = violationCodes;
    }
}
