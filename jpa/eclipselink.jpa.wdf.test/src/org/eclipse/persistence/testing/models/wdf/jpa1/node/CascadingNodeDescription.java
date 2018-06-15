/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.node;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
