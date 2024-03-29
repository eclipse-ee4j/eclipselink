/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     07/15/2010-2.2 Guy Pelletier
//       -311395 : Multiple lifecycle callback methods for the same lifecycle event
//     10/05/2012-2.4.1 Guy Pelletier
//       - 373092: Exceptions using generics, embedded key and entity inheritance
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostPersist;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;

@MappedSuperclass
// The reference to GenericTestInterface2 is added as a test for the fix for bug 411560
public class Beverage<U, PK> extends Consumable<PK> implements GenericTestInterface2<U, PK> {
    public static int BEVERAGE_POST_PERSIST_COUNT = 0;

    private PK id;

    public Beverage() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="BEVERAGE_TABLE_GENERATOR")
    @TableGenerator(
        name="BEVERAGE_TABLE_GENERATOR",
        table="CMP3_BEVERAGE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="BEVERAGE_SEQ")
    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    @PostPersist
    public void celebrateAgain() {
        BEVERAGE_POST_PERSIST_COUNT++;
    }
}
