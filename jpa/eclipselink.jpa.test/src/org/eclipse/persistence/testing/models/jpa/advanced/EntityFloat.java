/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// Based on reproduction scenario from issue #2301 (https://github.com/eclipse-ee4j/eclipselink/issues/2301)
@Entity
@Table(name = EntityFloat.TABLE_NAME)
public class EntityFloat {

    public static final String TABLE_NAME = "ADV_ENTITY_FLOAT";

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "HEIGHT")
    private float height;

    @Column(name = "LENGTH")
    private float length;

    @Column(name = "WIDTH")
    private float width;

    @Column(name = "DESCRIPTION")
    private String description;

    public EntityFloat() {
        this(-1, 0f, 0f, 0f, null);
    }

    public EntityFloat(int id, float length, float width, float height, String description) {
        this.id = id;
        this.length = length;
        this.width = width;
        this.height = height;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "EntityFloat{ id=%d, length=%f, width=%f, height=%f, description=\"%s\"}",
                id, length, width, height, description);
    }

}
