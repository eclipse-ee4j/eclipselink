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
//              James Sutherland - initial example
package org.eclipse.persistence.testing.models.jpa.performance2;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Represents an employee's job title.
 */
@Entity
@Table(name="P2_DEGREE")
public class Degree implements Serializable {
    @Id
    @Column(name = "DEGREE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Basic
    private String name;

    public Degree() {
    }

    public Degree(String name) {
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
