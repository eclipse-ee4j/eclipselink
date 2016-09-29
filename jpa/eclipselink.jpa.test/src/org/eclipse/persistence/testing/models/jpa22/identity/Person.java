/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/29/2016-2.7 Tomas Kraus
 *       - 426852: @GeneratedValue(strategy=GenerationType.IDENTITY) support in Oracle 12c
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa22.identity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Simple entity with ID as generated value using identity column
 */
@Entity
@Table(name="MOD_IDENT_PERSON")
public class Person {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column
    private String firstName;

    @Column String secondName;

    public Person() {
    }

    public Person(final String firstName, final String secondName) {
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setSecondName(final String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setFirstName(final String secondName) {
        this.secondName = secondName;
    }

}
