/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.mapping.model;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;
import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "parent")
@Cacheable(false)
@Multitenant(MultitenantType.TABLE_PER_TENANT)
@TenantTableDiscriminator(type = TenantTableDiscriminatorType.SCHEMA)
public class ParentMultitenant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<ChildMultitenant> children;

    public ParentMultitenant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ChildMultitenant> getChildren() {
        return children;
    }

    public void setChildren(List<ChildMultitenant> childMultitenants) {
        this.children = childMultitenants;
    }

}
