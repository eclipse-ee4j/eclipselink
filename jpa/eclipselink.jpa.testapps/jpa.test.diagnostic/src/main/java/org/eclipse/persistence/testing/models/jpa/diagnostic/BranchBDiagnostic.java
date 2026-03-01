/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.diagnostic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity implementation class for Entity: BranchA
 *
 */
@Entity
@Table(name = "BRANCHB_DIAGNOSTIC")
public class BranchBDiagnostic {

    private static final long serialVersionUID = 1L;

    protected int id;
    protected BranchADiagnostic branchA;

    /**
     * @return the id
     */
    @Id
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the branchA
     */
    @ManyToOne
    @JoinColumn(name = "BRANCHA_FK", nullable = true)
    public BranchADiagnostic getBranchA() {
        return branchA;
    }

    /**
     * @param branchA
     *            the branchA to set
     */
    public void setBranchA(BranchADiagnostic branchA) {
        this.branchA = branchA;
    }

    @Override
    public String toString() {
        return "BranchBDiagnostic{" +
                "id=" + id + '}';
    }
}
