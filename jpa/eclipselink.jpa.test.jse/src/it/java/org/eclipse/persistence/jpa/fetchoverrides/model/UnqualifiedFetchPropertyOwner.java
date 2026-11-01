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
package org.eclipse.persistence.jpa.fetchoverrides.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_UF_PROPERTY")
public class UnqualifiedFetchPropertyOwner extends UnqualifiedFetchBase {
    private UnqualifiedFetchTarget eagerTarget;
    private UnqualifiedFetchTarget lazyTarget;

    public UnqualifiedFetchPropertyOwner() {
    }

    public UnqualifiedFetchPropertyOwner(int id, UnqualifiedFetchTarget inherited,
            UnqualifiedFetchTarget eager, UnqualifiedFetchTarget lazy) {
        setId(id);
        setInheritedTarget(inherited);
        eagerTarget = eager;
        lazyTarget = lazy;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @Fetch
    @JoinColumn(name = "EAGER_ID")
    public UnqualifiedFetchTarget getEagerTarget() {
        return eagerTarget;
    }

    public void setEagerTarget(UnqualifiedFetchTarget eagerTarget) {
        this.eagerTarget = eagerTarget;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(type = FetchType.LAZY)
    @JoinColumn(name = "LAZY_ID")
    public UnqualifiedFetchTarget getLazyTarget() {
        return lazyTarget;
    }

    public void setLazyTarget(UnqualifiedFetchTarget lazyTarget) {
        this.lazyTarget = lazyTarget;
    }
}
