/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - initial contribution
package org.eclipse.persistence.testing.models.jpa.cacheable;

//This class is only used for metadata processing testing
// there is no corresponding table in the table creator for this class.

import jakarta.persistence.Cacheable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Noncacheable;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="JPA_ROTECTED_RELATIONSHIPS")
@Cacheable // defaults to true
public class ProtectedRelationshipsEntity {
    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    protected int id;
    protected String name;

    @OneToOne
    @Noncacheable
    protected CacheableTrueEntity cacheableFalse;

    @ManyToOne
    @Noncacheable
    protected CacheableTrueEntity cacheableProtected;

    @ManyToMany
    @Noncacheable
    protected List<CacheableTrueEntity> cacheableProtecteds;

    @OneToMany
    @JoinColumn(name="FORCED_PROTECTED")
    @Noncacheable
    protected List<CacheableTrueEntity> cacheableProtecteds2;

    @ElementCollection
    @Noncacheable
    protected List<String> elementCollection;

    @BasicCollection
    @Noncacheable
    protected List<String> basicCollection;

    public ProtectedRelationshipsEntity() {
        cacheableProtecteds = new ArrayList<>();
        cacheableProtecteds2 = new ArrayList<>();
        elementCollection = new ArrayList<>();
        basicCollection = new ArrayList<>();
    }

}
