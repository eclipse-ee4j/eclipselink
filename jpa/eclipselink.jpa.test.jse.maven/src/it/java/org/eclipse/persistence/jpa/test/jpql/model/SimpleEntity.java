/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
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
//     05/11/2018-2.7 Will Dazey
//       - 534515: Incorrect return type set for CASE functions
package org.eclipse.persistence.jpa.test.jpql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SimpleEntity {

    @Id @Column(name="KEY_CHAR")
    private String KeyString;

    @Column(name="ITEM_STRING1")
    private String itemString1;

    @Column(name="ITEM_STRING2")
    private String itemString2;

    @Column(name="ITEM_STRING3")
    private String itemString3;

    @Column(name="ITEM_STRING4")
    private String itemString4;

    @Column(name="ITEM_INTEGER1")
    private Integer itemInteger1;

    @Column(name="ITEM_INTEGER2")
    private int itemInteger2;

    public String getKeyString() {
        return KeyString;
    }

    public void setKeyString(String keyString) {
        KeyString = keyString;
    }

    public String getItemString1() {
        return itemString1;
    }

    public void setItemString1(String itemString1) {
        this.itemString1 = itemString1;
    }

    public String getItemString2() {
        return itemString2;
    }

    public void setItemString2(String itemString2) {
        this.itemString2 = itemString2;
    }

    public String getItemString3() {
        return itemString3;
    }

    public void setItemString3(String itemString3) {
        this.itemString3 = itemString3;
    }

    public String getItemString4() {
        return itemString4;
    }

    public void setItemString4(String itemString4) {
        this.itemString4 = itemString4;
    }

    public Integer getItemInteger1() {
        return itemInteger1;
    }

    public void setItemInteger1(Integer itemInteger1) {
        this.itemInteger1 = itemInteger1;
    }

    public int getItemInteger2() {
        return itemInteger2;
    }

    public void setItemInteger2(int itemInteger2) {
        this.itemInteger2 = itemInteger2;
    }
}
