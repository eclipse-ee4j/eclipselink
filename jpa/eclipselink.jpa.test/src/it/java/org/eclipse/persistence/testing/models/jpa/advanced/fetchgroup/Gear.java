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
 */package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

/**
 * A non-Entity Superclass.
 * The state here is non-persistent
 * This class should NOT be annotated with @MappedSuperclass or @Entity
 * See p.55 section 2.11.3 "Non-Entity Classes in the Entity Inheritance Hierarchy
 * of the JPA 2.0 JSR-317 specification
 */
// Contributors:
//     05/12/2009-2.1  mobrien - 2
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

/////
// A non-Entity Superclass.
// The state here is non-persistent
// This class should NOT be annotated with @MappedSuperclass or @Entity
// See p.55 section 2.11.3 "Non-Entity Classes in the Entity Inheritance Hierarchy
// of the JPA 2.0 JSR-317 specification
public class Gear {

    private Long nonPersistentObject;

    public Long getNonPersistentObject() {
        return nonPersistentObject;
    }

    public void setNonPersistentObject(Long nonPersistentObject) {
        this.nonPersistentObject = nonPersistentObject;
    }
}
