/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial Contribution
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import org.eclipse.persistence.config.QueryHints;

@Entity(name="JPA_CACHEABLE_FORCE_PROTECTED_WITH_COMPOSITE")
@Table(name="JPA_CACHEABLE_F_P_W_C")
public class ForceProtectedEntityWithComposite {
    protected int id;
    protected String name;
    protected ProtectedEmbeddable protectedEmbeddable;

    protected SharedEmbeddable sharedEmbeddable;

    public ForceProtectedEntityWithComposite() {
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString() {
        return "ForceProtectedEntityWithComposite: [" + name + "]";
    }

    /**
     * @return the protectedEmbeddable
     */
    @Embedded
    public ProtectedEmbeddable getProtectedEmbeddable() {
        return protectedEmbeddable;
    }

    /**
     * @param protectedEmbeddable the protectedEmbeddable to set
     */
    public void setProtectedEmbeddable(ProtectedEmbeddable protectedEmbeddable) {
        this.protectedEmbeddable = protectedEmbeddable;
    }

    /**
     * @return the sharedEmbeddable
     */
    @Embedded
    public SharedEmbeddable getSharedEmbeddable() {
        return sharedEmbeddable;
    }

    /**
     * @param sharedEmbeddable the sharedEmbeddable to set
     */
    public void setSharedEmbeddable(SharedEmbeddable sharedEmbeddable) {
        this.sharedEmbeddable = sharedEmbeddable;
    }



}
