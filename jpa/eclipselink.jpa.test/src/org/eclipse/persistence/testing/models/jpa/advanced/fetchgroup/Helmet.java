/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David Minsky - initial API and implementation
//     01/15/2016:2.7 Mythily Parthasarathy
//         - 485984-Added reference to Shelf entity
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

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
        this.properties = new HashMap<String, String>();
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
