/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     07/31/2008-1.1 Guy Pelletier
//       - 241388: JPA cache is not valid after a series of EntityManager operations
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


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

