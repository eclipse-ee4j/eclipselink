/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     20/11/2012-2.5 Guy Pelletier
//       - 394524: Invalid query key [...] in expression
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import static javax.persistence.CascadeType.PERSIST;
import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.PREFIX;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
