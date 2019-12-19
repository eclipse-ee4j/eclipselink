/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     12/17/2019 - Will Dazey
//       - 558414 : Add Oracle support for named parameters with stored procedures
package org.eclipse.persistence.jpa.test.storedproc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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