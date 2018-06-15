/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James - initial impl
package org.eclipse.persistence.testing.models.plsql;

/**
 * Used to test simple PLSQL record types.
 *
 * @author James
 */
public class Phone {
    protected String areaCode;
    protected String number;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Phone)) {
            return false;
        }
        Phone address = (Phone)object;
        if (this.areaCode != null && !this.areaCode.equals(address.areaCode)) {
            return false;
        }
        if (this.number != null && !this.number.equals(address.number)) {
            return false;
        }
        return true;
    }
}
