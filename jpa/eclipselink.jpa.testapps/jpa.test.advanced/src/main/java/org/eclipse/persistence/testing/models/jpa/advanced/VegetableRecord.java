/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name="CMP3_VEGETABLE_RECORD")
@IdClass(VegetablePKRecord.class)
public class VegetableRecord implements Serializable {
    private String name;
    private String color;
    private double cost;
    private String[] tags;
    private char type = '0';

    public VegetableRecord() {}

    @Id
    @Column(name="VEGETABLE_NAME")
    public String getName() {
        return name;
    }

    @Id
    @Column(name="VEGETABLE_COLOR")
    public String getColor() {
        return color;
    }

    public double getCost() {
        return cost;
    }

    @Column(columnDefinition="char(1)")
    public char getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setType(char aType) {
        this.type = aType;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
