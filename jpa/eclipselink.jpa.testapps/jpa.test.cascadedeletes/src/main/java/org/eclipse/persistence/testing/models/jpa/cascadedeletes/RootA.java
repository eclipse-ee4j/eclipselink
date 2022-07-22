/*
 * Copyright (c) 2018, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.eclipse.persistence.annotations.PrivateOwned;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.CascadeType.REMOVE;

/**
 * Entity implementation class for Entity: RootA
 *
 */

@Entity
public class RootA implements Serializable, PersistentIdentity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    protected int id;
@PrivateOwned
    @OneToMany(fetch = LAZY, cascade = REMOVE)
    protected List<BranchA> branchAs;

    @OneToOne(fetch = LAZY, cascade = REMOVE)
    protected BranchB branchB;

    public RootA() {
        super();
        this.branchAs = new ArrayList<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the branchAs
     */
    public List<BranchA> getBranchAs() {
        return branchAs;
    }

    /**
     * @param branchAs
     *            the branchAs to set
     */
    public void setBranchAs(List<BranchA> branchAs) {
        this.branchAs = branchAs;
    }

    /**
     * @return the branchB
     */
    public BranchB getBranchB() {
        return branchB;
    }

    /**
     * @param branchB
     *            the branchB to set
     */
    public void setBranchB(BranchB branchB) {
        this.branchB = branchB;
    }

    public boolean checkTreeForRemoval(EntityManager em) {
        boolean exists = em.find(BranchB.class, this.getId()) != null;
        if (!exists) {
            exists = exists || (this.branchB != null && this.branchB.checkTreeForRemoval(em));
            for (BranchA branchA : this.branchAs) {
                exists = exists || branchA.checkTreeForRemoval(em);
            }
        }
        return exists;
    }
}
