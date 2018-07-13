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
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

/**
 * A non-Entity non-MappedSuperclass Superclass is a Transient class
 * The state here is non-persistent
 * This class should NOT be annotated with @MappedSuperclass or @Entity
 *
 * This class will show as a BasicType even though it has attributes
 * BasicTypeImpl@2540449:Position [ javaType: class org.eclipse.persistence.testing.models.jpa.metamodel.Position]
 *
 * The attributes here will not be inherited by Entity or MappedSuperclass subclasses.
 * Any attempt to access fields here via subclasses will result in an expecte IllegalArgumentException
 */
public class Position {

    private Long nonPersistentObject;

    public Long getNonPersistentObject() {
        return nonPersistentObject;
    }

    public void setNonPersistentObject(Long nonPersistentObject) {
        this.nonPersistentObject = nonPersistentObject;
    }
}
