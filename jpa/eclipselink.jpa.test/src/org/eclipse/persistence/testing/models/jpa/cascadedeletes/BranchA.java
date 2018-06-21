/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Entity implementation class for Entity: BranchA
 *
 */
@Entity
public class BranchA implements Serializable, PersistentIdentity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    protected int id;

    @ManyToOne(fetch = LAZY, cascade = REMOVE)
    protected BranchA branchA;

    @PrivateOwned
    @OneToMany(fetch = LAZY, cascade = REMOVE, mappedBy = "branchA")
    protected List<LeafA> leafs;

    @PrivateOwned
    @OneToMany(fetch = LAZY, cascade = REMOVE)
    protected List<LeafA> secondSet;

    /**
     * @return the id
     */
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
    public BranchA getBranchA() {
        return branchA;
    }

    /**
     * @param branchA
     *            the branchA to set
     */
    public void setBranchA(BranchA branchA) {
        this.branchA = branchA;
    }

    /**
     * @return the leafs
     */
    public List<LeafA> getLeafs() {
        return leafs;
    }

    /**
     * @param leafs
     *            the leafs to set
     */
    public void setLeafs(List<LeafA> leafs) {
        this.leafs = leafs;
    }

    public BranchA() {
        super();
        this.leafs = new ArrayList<LeafA>();
        this.secondSet = new ArrayList<LeafA>();
    }

    /**
     * @return the secondSet
     */
    public List<LeafA> getSecondSet() {
        return secondSet;
    }

    /**
     * @param secondSet the secondSet to set
     */
    public void setSecondSet(List<LeafA> secondSet) {
        this.secondSet = secondSet;
    }



    public boolean checkTreeForRemoval(EntityManager em) {
        boolean exists = em.find(this.getClass(), this.getId()) != null;
        if (!exists) {
            exists = exists || this.branchA.checkTreeForRemoval(em);
            for (LeafA leafA : this.leafs) {
                exists = exists || (leafA != null && leafA.checkTreeForRemoval(em));
            }
            for (LeafA leafA : this.secondSet) {
                exists = exists || (leafA != null && leafA.checkTreeForRemoval(em));
            }
        }
        return exists;
    }
}
