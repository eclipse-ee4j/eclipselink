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
//     07/18/2019 - Will Dazey
//       - Initial implementation
package org.eclipse.persistence.jpa.test.property.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class GenericEntity {

    @Id
    @Column(name = "KEY_CHAR")
    private String KeyString;

    @Column(name = "ITEM_STRING1")
    private String itemString1;

    @Column(name = "ITEM_INTEGER1")
    private Integer itemInteger1;

    @Column(name = "ITEM_BOOLEAN1")
    private boolean itemBoolean1;

    @Column(name = "ITEM_DATE1")
    private Date itemDate1;

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

    public Integer getItemInteger1() {
        return itemInteger1;
    }

    public void setItemInteger1(Integer itemInteger1) {
        this.itemInteger1 = itemInteger1;
    }

    public boolean isItemBoolean1() {
        return itemBoolean1;
    }

    public void setItemBoolean1(boolean itemBoolean1) {
        this.itemBoolean1 = itemBoolean1;
    }

    public Date getItemDate1() {
        return itemDate1;
    }

    public void setItemDate1(Date itemDate1) {
        this.itemDate1 = itemDate1;
    }
}
