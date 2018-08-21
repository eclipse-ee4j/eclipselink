/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;

public class Person_King {
    private int id;
    private String name;

    public Person_King() {
        super();
    }

    public static Person_King exp1() {
        Person_King p1 = new Person_King();
        p1.setId(1);
        p1.setName("Jack");
        return p1;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int theId) {
        id = theId;
    }

    public void setName(String theName) {
        name = theName;

    }
}
