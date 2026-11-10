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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Alternative mappings read the existing fixture row without creating another schema. */
@Entity
@Table(name = "JPA_UF_OWNER")
public class UnqualifiedFetchDefaultOwner {
    @Id
    private int id;

    @ManyToOne
    @Fetch(type = FetchType.EAGER)
    @JoinColumn(name = "EAGER_MANY_ID")
    private UnqualifiedFetchTarget eagerMany;

    @ManyToOne
    @Fetch(type = FetchType.LAZY)
    @JoinColumn(name = "LAZY_MANY_ID")
    private UnqualifiedFetchTarget lazyMany;

    @OneToOne(fetch = FetchType.DEFAULT)
    @Fetch(type = FetchType.EAGER)
    @JoinColumn(name = "EAGER_ONE_ID")
    private UnqualifiedFetchTarget eagerOne;

    @OneToOne(fetch = FetchType.DEFAULT)
    @Fetch(type = FetchType.LAZY)
    @JoinColumn(name = "LAZY_ONE_ID")
    private UnqualifiedFetchTarget lazyOne;

    public UnqualifiedFetchTarget getEagerMany() {
        return eagerMany;
    }

    public UnqualifiedFetchTarget getLazyMany() {
        return lazyMany;
    }

    public UnqualifiedFetchTarget getEagerOne() {
        return eagerOne;
    }

    public UnqualifiedFetchTarget getLazyOne() {
        return lazyOne;
    }
}
