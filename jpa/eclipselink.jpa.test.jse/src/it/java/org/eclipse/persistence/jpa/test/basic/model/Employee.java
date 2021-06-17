/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
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
//     12/19/2014 - Dalia Abo Sheasha
//       - 454917 : Added a test to use the IDENTITY strategy to generate values

package org.eclipse.persistence.jpa.test.basic.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name="oepjtbmEmployee")
@NamedQuery(name="Employee.findAll", query="SELECT e FROM Employee e")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Version
    int version;

    public Employee() {

    }

    public int getId() {
        return id;
    }
}
