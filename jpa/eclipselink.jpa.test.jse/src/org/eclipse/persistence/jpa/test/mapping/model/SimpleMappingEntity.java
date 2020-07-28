/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     09/12/2019 - Will Dazey
//       - 471144: Add support for AggregateObjectMappings to eclipselink.cursor impl
package org.eclipse.persistence.jpa.test.mapping.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
