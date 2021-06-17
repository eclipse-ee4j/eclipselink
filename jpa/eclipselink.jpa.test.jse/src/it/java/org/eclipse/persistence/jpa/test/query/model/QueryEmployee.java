/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.query.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name="QUERY_EMPLOYEE")
@NamedQuery(name="QueryEmployee.findAll", query="SELECT e FROM QueryEmployee e")
public class QueryEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Version
    int version;

    public QueryEmployee() { }

    public int getId() {
        return id;
    }
}
