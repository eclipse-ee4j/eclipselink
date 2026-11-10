/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.fetchdefaults.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_DTF_PROPERTY")
public class DefaultFetchPropertyOwner extends DefaultFetchMappedSuperclass {
    private DefaultFetchTarget many;
    private DefaultFetchTarget one;

    public DefaultFetchPropertyOwner() {
    }

    public DefaultFetchPropertyOwner(int id, DefaultFetchTarget inherited, DefaultFetchTarget many, DefaultFetchTarget one) {
        setId(id);
        setInheritedTarget(inherited);
        this.many = many;
        this.one = one;
    }

    @ManyToOne(fetch = FetchType.DEFAULT)
    @JoinColumn(name = "MANY_ID")
    public DefaultFetchTarget getMany() {
        return many;
    }

    public void setMany(DefaultFetchTarget many) {
        this.many = many;
    }

    @OneToOne
    @JoinColumn(name = "ONE_ID")
    public DefaultFetchTarget getOne() {
        return one;
    }

    public void setOne(DefaultFetchTarget one) {
        this.one = one;
    }
}
