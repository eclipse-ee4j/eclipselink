/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/15/2021 - Will Dazey
 *       - 570378 : NullPointerException from Embedded Temporal
 ******************************************************************************/
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
