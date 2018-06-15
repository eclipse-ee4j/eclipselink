/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.island;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

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
