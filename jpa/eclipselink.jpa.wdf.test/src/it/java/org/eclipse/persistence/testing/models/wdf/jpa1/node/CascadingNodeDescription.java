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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_CASC_NODE_DESC")
public class CascadingNodeDescription {

    @Id
    protected int id;

    @OneToOne(mappedBy = "description")
    protected CascadingNode node;

    @Basic
    @Column(name = "DESC_TEXT")
    protected String description;

    public CascadingNodeDescription() {
    }

    public CascadingNodeDescription(int anId, CascadingNode aNode, String aDescription) {
        id = anId;
        node = aNode;
        description = aDescription;
    }

    public int getId() {
        return id;
    }

    public CascadingNode getNode() {
        return node;
    }

    public void setNode(CascadingNode node) {
        this.node = node;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
