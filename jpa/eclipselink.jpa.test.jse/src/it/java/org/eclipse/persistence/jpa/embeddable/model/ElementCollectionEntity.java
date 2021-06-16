/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.jpa.embeddable.model;

import java.util.Date;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyTemporal;
import jakarta.persistence.TemporalType;

@Entity
public class ElementCollectionEntity {

    @Id
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "EntMapDateTemporal")
    @MapKeyColumn(name = "mykey")
    @MapKeyTemporal(TemporalType.DATE)
    private Map<Date, ElementCollectionEmbeddableTemporal> mapKeyTemporalValueEmbed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Date, ElementCollectionEmbeddableTemporal> getMapKeyTemporalValueEmbed() {
        return mapKeyTemporalValueEmbed;
    }

    public void setMapKeyTemporalValueEmbed(Map<Date, ElementCollectionEmbeddableTemporal> mapKeyTemporalValueEmbed) {
        this.mapKeyTemporalValueEmbed = mapKeyTemporalValueEmbed;
    }
}
