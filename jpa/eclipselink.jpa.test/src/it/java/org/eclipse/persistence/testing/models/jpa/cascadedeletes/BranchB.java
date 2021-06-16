/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
public class BranchB implements Serializable, PersistentIdentity{
    @Id
    @GeneratedValue
    protected int id;

    @PrivateOwned
    @OneToMany(fetch = LAZY, cascade = REMOVE)
    @OrderBy("id")
    protected List<BranchB> branchBs;

    @ManyToMany(fetch = LAZY, cascade = REMOVE)
    protected List<LeafB> leafBs;

    public BranchB() {
        this.branchBs = new ArrayList<BranchB>();
        this.leafBs = new ArrayList<LeafB>();
    }
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
     * @return the branchBs
     */
    public List<BranchB> getBranchBs() {
        return branchBs;
    }

    /**
     * @param branchBs
     *            the branchBs to set
     */
    public void setBranchBs(List<BranchB> branchBs) {
        this.branchBs = branchBs;
    }

    /**
     * @return the leafBs
     */
    public List<LeafB> getLeafBs() {
        return leafBs;
    }

    /**
     * @param leafBs
     *            the leafBs to set
     */
    public void setLeafBs(List<LeafB> leafBs) {
        this.leafBs = leafBs;
    }

    public boolean checkTreeForRemoval(EntityManager em){
        boolean exists = em.find(this.getClass(), this.getId())!= null;
        if (! exists){
            for (BranchB subBs: this.branchBs){
                exists = exists || subBs.checkTreeForRemoval(em);
            }
            for (LeafB leafB : this.leafBs){
                exists = exists || leafB.checkTreeForRemoval(em);
            }
        }
        return exists;
    }


}
