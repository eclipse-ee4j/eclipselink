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
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

public class PhoneNumberPK  {
    public Integer id;
    public String type;

    public PhoneNumberPK() {}

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * equals: Answer true if the ids are equal
     */
    public boolean equals(Object anotherPhoneNumber) {
        if (anotherPhoneNumber.getClass() != PhoneNumberPK.class) {
            return false;
        }

        if (! getType().equals(((PhoneNumberPK) anotherPhoneNumber).getType())) {
            return false;
        }

        return (getId().equals(((PhoneNumberPK) anotherPhoneNumber).getId()));
    }
}

