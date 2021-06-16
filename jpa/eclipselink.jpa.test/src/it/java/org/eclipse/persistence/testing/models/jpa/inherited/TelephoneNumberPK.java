/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.inherited;

public class TelephoneNumberPK  {
    public String type;
    protected String number;
    private String areaCode;

    public TelephoneNumberPK() {}

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object anotherTelephoneNumberPK) {
        if (anotherTelephoneNumberPK.getClass() != TelephoneNumberPK.class) {
            return false;
        }

        TelephoneNumberPK telephoneNumberPK = (TelephoneNumberPK) anotherTelephoneNumberPK;

        return (
            telephoneNumberPK.getAreaCode().equals(getAreaCode()) &&
            telephoneNumberPK.getNumber().equals(getNumber()) &&
            telephoneNumberPK.getType().equals(getType())
        );

    }
}
