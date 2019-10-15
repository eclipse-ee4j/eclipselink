/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/18/2018: Jan Vissers
//       - #539822: JPA Canonical metamodel not processing metamodelMappedSuperclasses
package org.eclipse.persistence.jpa.test.canonical;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DomainPersistable implements DomainInterface {

    // Holds all standard fields, mapped to something that is going to be persisted in the database

    @Id
    private Long id;
    private String name;

    @Override
    public Long getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }
}
