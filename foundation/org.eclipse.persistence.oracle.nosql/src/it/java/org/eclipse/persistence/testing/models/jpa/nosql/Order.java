/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
