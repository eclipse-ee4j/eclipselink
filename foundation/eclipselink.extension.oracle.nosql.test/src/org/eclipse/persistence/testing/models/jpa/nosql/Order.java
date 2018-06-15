/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.nosql;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.eclipse.persistence.nosql.annotations.NoSql;
import org.eclipse.persistence.annotations.UuidGenerator;

/**
 * Model order class, maps to ORDER record.
 */
@Entity
@NoSql
public class Order {
    @Id
    @Column(name="@id")
    @UuidGenerator(name="uuid")
    @GeneratedValue(generator="uuid")
    public String id;
    public String orderedBy;
    public Address address;
    @OneToOne
    public Customer customer;
    @ElementCollection
    public List<LineItem> lineItems = new ArrayList();
    @ElementCollection
    public List<String> comments = new ArrayList();

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ", " + comments + ")";
    }
}
