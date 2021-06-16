/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Gordon Yorke - Initial Contribution
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.QueryHint;
import jakarta.persistence.TableGenerator;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Noncacheable;
import org.eclipse.persistence.config.QueryHints;

public class ProtectedRelationshipsEntity {

    protected int id;
    protected String name;

    protected CacheableTrueEntity cacheableFalse;

    protected CacheableTrueEntity cacheableProtected;

    protected List<CacheableTrueEntity> cacheableProtecteds;

    protected List<CacheableTrueEntity> cacheableProtecteds2;

    protected List<String> elementCollection;

    protected List<String> basicCollection;

    public ProtectedRelationshipsEntity() {
        cacheableProtecteds = new ArrayList<CacheableTrueEntity>();
        cacheableProtecteds2 = new ArrayList<CacheableTrueEntity>();
        elementCollection = new ArrayList<String>();
        basicCollection = new ArrayList<String>();
    }

}
