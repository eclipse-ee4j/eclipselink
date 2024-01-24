/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018, 2024 IBM Corporation. All rights reserved.
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
//     03/14/2018-2.7 Will Dazey
//       - 500753: Synchronize initialization of InsertQuery
package org.eclipse.persistence.jpa.test.concurrency.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name="CONCURR_USER")
@NamedQuery(name = "CONCURR_CASE_QUERY", query = "SELECT CASE WHEN (COUNT(e) > 0) THEN true ELSE false END FROM User e WHERE e.id = :id")
public class User {

    @Id private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_tags", joinColumns = @JoinColumn(name = "user_id"))
    private List<UserTag> tags = new ArrayList<>();

    public User() {}
    
    public User(int id) {
        this.setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<UserTag> getTags() {
        return tags;
    }

    public void setTags(List<UserTag> tags) {
        this.tags = tags;
    }
}
