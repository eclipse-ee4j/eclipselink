/*
 * Copyright (c) 2005, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.island;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_ISLAND")
/* Test PseudoInheritance without subclasses */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Cacheable(true)
// FIXME invalid after 2 seconds
public class Island {
    @Id
    @GeneratedValue
    private int id;

    private String name;

    public Island(String aName) {
        name = aName;
    }

    public Island() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
