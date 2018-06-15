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
//     Oracle - initial API and implementation from Oracle TopLink
//     08/27/2008-1.1 Guy Pelletier
//       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Embeddable
public class Name implements Cloneable {
    private int id;
    private String firstName;
    private String lastName;

    public Name() {}

    @Column(name="FNAME")
    public String getFirstName() {
        return firstName;
    }

    @GeneratedValue(strategy = IDENTITY)
    public int getId() {
        return id;
    }

    @Column(name="LNAME")
    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return firstName + " " + lastName;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
    }
}
