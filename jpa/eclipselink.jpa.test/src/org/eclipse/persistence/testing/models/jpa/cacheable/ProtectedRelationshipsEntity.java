/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Noncacheable;
import org.eclipse.persistence.config.QueryHints;

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
        cacheableProtecteds = new ArrayList<CacheableTrueEntity>();
        cacheableProtecteds2 = new ArrayList<CacheableTrueEntity>();
        elementCollection = new ArrayList<String>();
        basicCollection = new ArrayList<String>();
    }

}
