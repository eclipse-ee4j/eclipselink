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
//     12/30/2010-2.3 Guy Pelletier submitted for Paul Fullbright
//       - 312253: Descriptor exception with Embeddable on DDL gen
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static jakarta.persistence.AccessType.PROPERTY;

import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
