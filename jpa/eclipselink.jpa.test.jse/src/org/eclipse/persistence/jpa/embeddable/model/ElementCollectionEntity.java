/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.embeddable.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyTemporal;
import javax.persistence.TemporalType;

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
