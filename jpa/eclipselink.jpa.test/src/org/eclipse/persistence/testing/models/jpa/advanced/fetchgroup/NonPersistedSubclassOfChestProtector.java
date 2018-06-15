/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

/**
 * A non-Entity Superclass.
 * The state here is non-persistent
 * This class should NOT be annotated with @MappedSuperclass or @Entity
 * See p.55 section 2.11.3 "Non-Entity Classes in the Entity Inheritance Hierarchy
 * of the JPA 2.0 JSR-317 specification
 */
public class NonPersistedSubclassOfChestProtector extends ChestProtector {

    private long nonPersistentPrimitive;

    public long getNonPersistentPrimitive() {
        return nonPersistentPrimitive;
    }

    public void setNonPersistentPrimitive(long nonPersistentPrimitive) {
        this.nonPersistentPrimitive = nonPersistentPrimitive;
    }

}
