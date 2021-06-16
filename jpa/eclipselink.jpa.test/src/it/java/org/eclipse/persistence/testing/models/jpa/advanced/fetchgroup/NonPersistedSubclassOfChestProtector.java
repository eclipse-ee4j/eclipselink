/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
