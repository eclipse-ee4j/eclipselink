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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.TABLE;

/**
 * @author Wonseok Kim
 */
@Embeddable
public class CKeyEntityCPK {
    @GeneratedValue(strategy = TABLE, generator = "CKEYENTITY_TABLE_GENERATOR")
    @Column(name = "SEQ")
    public int seq;

    @Column(name = "ROLE_", length=64)
    public String role;


    public CKeyEntityCPK() {
    }

    public CKeyEntityCPK(String role) {
        this.role = role;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CKeyEntityCPK that = (CKeyEntityCPK) o;

        return seq == that.seq && role.equals(that.role);
    }

    public int hashCode() {
        int result;
        result = seq;
        result = 31 * result + role.hashCode();
        return result;
    }
}
