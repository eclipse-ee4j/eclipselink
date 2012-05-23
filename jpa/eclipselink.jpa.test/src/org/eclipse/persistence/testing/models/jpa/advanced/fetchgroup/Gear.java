/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/12/2009-2.1  mobrien - 2
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

/**
 * A non-Entity Superclass.
 * The state here is non-persistent
 * This class should NOT be annotated with @MappedSuperclass or @Entity
 * See p.55 section 2.11.3 "Non-Entity Classes in the Entity Inheritance Hierarchy
 * of the JPA 2.0 JSR-317 specification
 */
public class Gear {

    private Long nonPersistentObject;

    public Long getNonPersistentObject() {
        return nonPersistentObject;
    }

    public void setNonPersistentObject(Long nonPersistentObject) {
        this.nonPersistentObject = nonPersistentObject;
    }
}
