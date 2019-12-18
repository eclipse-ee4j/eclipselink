/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/17/2019 - Will Dazey
 *       - 558414 : Add Oracle support for named parameters with stored procedures
 ******************************************************************************/
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