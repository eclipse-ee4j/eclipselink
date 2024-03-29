/*
 * Copyright (c) 2005, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2024 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.fancy;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_PLANT")
@SequenceGenerator(name = "SequenceGeneratorInc3", sequenceName = "testSequenceInc3", allocationSize = 3)
public class Plant implements Serializable {
    private static final long serialVersionUID = 1L;

    public Plant(String aName) {
        name = aName;
    }

    private Integer id;
    private String name;

    public Plant() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorInc3")
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    protected void setId(Integer aId) {
        id = aId;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Plant other) {
            return other.name.equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }
}
