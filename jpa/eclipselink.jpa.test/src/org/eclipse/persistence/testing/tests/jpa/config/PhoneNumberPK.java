/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.config;

/**
 * JPA scripting API implementation helper class.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PhoneNumberPK  {
    public Integer id;
    public String type;

    public PhoneNumberPK() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
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
        return (getId().equals(((PhoneNumberPK)anotherPhoneNumber).getId()));
    }
}
