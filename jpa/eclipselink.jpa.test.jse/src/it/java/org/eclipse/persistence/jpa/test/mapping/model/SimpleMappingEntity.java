/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     09/12/2019 - Will Dazey
//       - 471144: Add support for AggregateObjectMappings to eclipselink.cursor impl
package org.eclipse.persistence.jpa.test.mapping.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SimpleMappingEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int mappingField1;

    @Embedded
    private SimpleMappingEmbeddable aggregateObjectMapping;

    public SimpleMappingEntity() { }

    public SimpleMappingEntity(int mappingField1, SimpleMappingEmbeddable aggregateObjectMapping) {
        this.mappingField1 = mappingField1;
        this.aggregateObjectMapping = aggregateObjectMapping;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getMappingField1() {
        return mappingField1;
    }
    public void setMappingField1(int mappingField1) {
        this.mappingField1 = mappingField1;
    }
    public SimpleMappingEmbeddable getAggregateObjectMapping() {
        return aggregateObjectMapping;
    }
    public void setAggregateObjectMapping(SimpleMappingEmbeddable aggregateObjectMapping) {
        this.aggregateObjectMapping = aggregateObjectMapping;
    }
}
