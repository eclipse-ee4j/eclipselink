/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     20/11/2012-2.5 Guy Pelletier
//       - 394524: Invalid query key [...] in expression
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import static jakarta.persistence.CascadeType.PERSIST;
import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.PREFIX;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;

@Entity
@Table(name="JPA_SUPPORTER_INFO")
@Multitenant(TABLE_PER_TENANT)
@TenantTableDiscriminator(type=PREFIX)
public class SupporterInfo {
    @Id
    @GeneratedValue
    public long id;

    @Column(name="DESCRIP")
    public String description;

    @OneToOne(cascade=PERSIST)
    @JoinColumn(name="SUPPORTER_INFO_SUB_ID")
    public SupporterInfoSub subInfo;

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public SupporterInfoSub getSubInfo() {
        return subInfo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSubInfo(SupporterInfoSub subInfo) {
        this.subInfo = subInfo;
    }
}
