/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/17/2010-2.2 Guy Pelletier
//       - 329008: Support dynamic context creation without persistence.xml
package org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic;

import java.sql.Time;
import java.io.Serializable;

public class MyDynamicEntity implements Serializable {

    private Integer id;
    private String firstName;
    private String lastName;

    public MyDynamicEntity () {}

    public MyDynamicEntity (String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return "DynamicEntity: " + getId();
    }
}

