/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.node;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "TMP_CASC_NODE")
public class CascadingNode {

    @Id
    protected int id;

    @ManyToOne(cascade = { PERSIST, MERGE, REFRESH })
    @JoinColumn(name = "PARENT")
    protected CascadingNode parent;

    @OneToMany(mappedBy = "parent", cascade = { ALL })
    protected Set<CascadingNode> children;

    @OneToOne(cascade = { ALL })
    @JoinColumn(name = "DESCRIPTION")
    protected CascadingNodeDescription description;

    @Transient
    protected boolean postUpdateCalled;

    public CascadingNode() {

    }

    public CascadingNode(int anId, CascadingNode aParent) {
        id = anId;
        parent = aParent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public int getId() {
        return id;
    }

    public CascadingNode getParent() {
        return parent;
    }

    public void setParent(CascadingNode aParent) {
        parent = aParent;
    }

    public Set<CascadingNode> getChildren() {
        return children;
    }

    public void setChildren(Set<CascadingNode> theChildren) {
        children = theChildren;
    }

    public CascadingNodeDescription getDescription() {
        return description;
    }

    public void setDescription(CascadingNodeDescription description) {
        this.description = description;
    }

    public void addChild(CascadingNode child) {
        if (children == null) {
            children = new HashSet<CascadingNode>();
        }
        children.add(child);
    }

    public void clearPostUpdate() {
        postUpdateCalled = false;
    }

    public void postUpdate() {
        postUpdateCalled = true;

    }

    public boolean postUpdateWasCalled() {
        return postUpdateCalled;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CascadingNode) {
            CascadingNode other = (CascadingNode) obj;
            return id == other.id;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id;
    }

}
