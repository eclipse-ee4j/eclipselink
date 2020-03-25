/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2010, 2015 Frank Schwarz. All rights reserved.
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
//     11/23/2010-2.2 Frank Schwarz
//       - 328774: TABLE_PER_CLASS-mapped key of a java.util.Map does not work for querying
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyJoinColumn;

@Entity
public class DesignPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Map<ProgrammingLanguage, CodeExample> codeExamples = new HashMap<ProgrammingLanguage, CodeExample>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<ProgrammingLanguage, CodeExample> getCodeExamples() {
        return codeExamples;
    }

    public void setCodeExamples(Map<ProgrammingLanguage, CodeExample> codeExamples) {
        this.codeExamples = codeExamples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
