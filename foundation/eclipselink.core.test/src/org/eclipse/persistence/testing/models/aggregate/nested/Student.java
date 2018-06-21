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
package org.eclipse.persistence.testing.models.aggregate.nested;

public class Student {
    protected Integer id;
    protected String firstName;
    protected String lastName;
    protected Guardian guardian;

    public String getFirstName() {
        return firstName;
    }

    public Guardian getGuardian() {
        return guardian;
    }

    public Integer getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String param1) {
        firstName = param1;
    }

    public void setGuardian(Guardian param1) {
        guardian = param1;
    }

    public void setId(Integer param1) {
        id = param1;
    }

    public void setLastName(String param1) {
        lastName = param1;
    }
}
