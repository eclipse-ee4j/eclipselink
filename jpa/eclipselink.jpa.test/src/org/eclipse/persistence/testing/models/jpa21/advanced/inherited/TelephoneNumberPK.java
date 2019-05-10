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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa21.advanced.inherited;

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
