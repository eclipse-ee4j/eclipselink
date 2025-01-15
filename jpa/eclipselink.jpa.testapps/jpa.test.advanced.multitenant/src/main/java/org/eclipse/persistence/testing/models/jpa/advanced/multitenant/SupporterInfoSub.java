/*
 * Copyright (c) 2012, 2025 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;

import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.PREFIX;

@Entity
@Table(name="JPA_SUPPORTER_INFO_SUB")
@Multitenant(TABLE_PER_TENANT)
@TenantTableDiscriminator(type=PREFIX)
public class SupporterInfoSub {
    @Id
    @GeneratedValue
    public long id;

    @Column(name="SUB_DESCRIP")
    public String subDescription;

    public long getId() {
        return id;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSubDescription(String description) {
        this.subDescription = description;
    }
}
