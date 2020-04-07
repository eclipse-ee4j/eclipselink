/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     07/31/2008-1.1 Guy Pelletier
//       - 241388: JPA cache is not valid after a series of EntityManager operations
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;


@Entity
@Table(name="FIELD_CHILD")
public class Child implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Version
    @Column
    private Integer version;

    @ManyToOne(cascade = {CascadeType.ALL}, targetEntity = Parent.class, fetch=FetchType.LAZY)
    private ParentInterface parent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createdOn = new Date();

    public Child() {}

    public Integer getId() {
        return this.id;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

    public ParentInterface getParent() {
        return parent;
    }

    public int getVersion() {
        return version;
    }

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParent(ParentInterface parent) {
        this.parent = parent;
    }
}

