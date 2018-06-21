/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/30/2010-2.3 Guy Pelletier submitted for Paul Fullbright
//       - 312253: Descriptor exception with Embeddable on DDL gen
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.AccessType.PROPERTY;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Access(PROPERTY)
public class EntityType {

    private String id;

    private List<EmbeddableType> assortment;

    @Id
    protected String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    @ElementCollection
    protected List<EmbeddableType> getAssortment() {
        return this.assortment;
    }

    protected void setAssortment(List<EmbeddableType> assortment) {
        this.assortment = assortment;
    }
}
