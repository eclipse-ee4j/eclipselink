/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.test.diagnostic.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@NamedQueries({
        @NamedQuery(name = "findBranchADiagnosticById", query = "SELECT bd FROM BranchADiagnostic bd " + "WHERE bd.id = :id")}
)
@Entity
@Table(name = "BRANCHA_DIAGNOSTIC")
public class BranchADiagnostic {
    protected int id;

    protected List<BranchBDiagnostic> branchBs;

    public BranchADiagnostic() {
        this.branchBs = new ArrayList<>();
    }
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
     * @return the branchBs
     */
    @OneToMany(mappedBy = "branchA")
    public List<BranchBDiagnostic> getBranchBs() {
        return branchBs;
    }

    /**
     * @param branchBs
     *            the branchBs to set
     */
    public void setBranchBs(List<BranchBDiagnostic> branchBs) {
        this.branchBs = branchBs;
    }

    @Override
    public String toString() {
        return "BranchADiagnostic{" +
                "id=" + id + '}';
    }
}
