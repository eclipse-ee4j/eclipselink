/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.nosql;

import java.util.*;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import org.eclipse.persistence.nosql.annotations.NoSql;
import org.eclipse.persistence.annotations.UuidGenerator;

/**
 * Model order class, maps to ORDER record.
 */
@Entity
@NoSql
public class Order {
    @Id
    @Column(name="id")
    @UuidGenerator(name="uuid")
    @GeneratedValue(generator="uuid")
    public String id;
    public String orderedBy;
    public Address address;
    @OneToOne
    public Customer customer;
    @ElementCollection
    public List<LineItem> lineItems = new ArrayList<>();
    @ElementCollection
    public List<String> comments = new ArrayList<>();

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ", " + comments + ")";
    }
}
