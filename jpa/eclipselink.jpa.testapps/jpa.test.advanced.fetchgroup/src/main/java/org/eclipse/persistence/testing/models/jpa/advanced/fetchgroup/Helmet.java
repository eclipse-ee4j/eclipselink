/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     David Minsky - initial API and implementation
//     01/15/2016:2.7 Mythily Parthasarathy
//         - 485984-Added reference to Shelf entity
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.PrivateOwned;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="JPA_HELMET")
public class Helmet {

    @Id
    protected int id;

    @Basic(fetch=FetchType.LAZY)
    protected String color;

    @ElementCollection
    @PrivateOwned
    @CollectionTable(
            name="JPA_HELMET_PROPERTIES",
            joinColumns={
                @JoinColumn(name="HELMET_ID", referencedColumnName="ID")
            })
    @Column(name="PROPERTY_VALUE")
    @MapKeyColumn(name="PROPERTY_NAME")
    protected Map<String, String> properties;

    @ManyToOne(targetEntity = Shelf.class)
    @JoinColumn(name = "SHELF_ID", referencedColumnName = "ID")
    private Shelf shelf;

    public Helmet() {
        super();
        this.properties = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(String propertyName, String propertyValue) {
        getProperties().put(propertyName, propertyValue);
    }

    public void removeProperty(String propertyName) {
        getProperties().remove(propertyName);
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public Shelf getShelf() {
        return this.shelf;
    }
}
