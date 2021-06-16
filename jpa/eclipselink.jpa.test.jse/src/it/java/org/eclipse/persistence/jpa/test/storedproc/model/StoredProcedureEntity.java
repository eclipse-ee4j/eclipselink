/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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

// Contributors:
//     12/17/2019 - Will Dazey
//       - 558414 : Add Oracle support for named parameters with stored procedures
package org.eclipse.persistence.jpa.test.storedproc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Simple Entity that exists just so we can use the table in stored procedures
 */
@Entity
@Table(name="STORED_PROCEDURE_ENTITY")
public class StoredProcedureEntity {

    @Id @Column(name="KEY_CHAR")
    private String keyString;

    @Column(name="ITEM_STRING1")
    private String itemString1;

    @Column(name="ITEM_INTEGER1")
    private Integer itemInteger1;

    public StoredProcedureEntity() { }

    public StoredProcedureEntity(String keyString, String itemString1, Integer itemInteger1) {
        this.keyString = keyString;
        this.itemString1 = itemString1;
        this.itemInteger1 = itemInteger1;
    }

    public String getKeyString() {
        return keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    public String getItemString1() {
        return itemString1;
    }

    public void setItemString1(String itemString1) {
        this.itemString1 = itemString1;
    }

    public Integer getItemInteger1() {
        return itemInteger1;
    }

    public void setItemInteger1(Integer itemInteger1) {
        this.itemInteger1 = itemInteger1;
    }
}
