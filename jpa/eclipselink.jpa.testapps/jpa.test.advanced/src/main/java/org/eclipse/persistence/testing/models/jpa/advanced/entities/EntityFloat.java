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
package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

// Based on reproduction scenario from issue #2301 (https://github.com/eclipse-ee4j/eclipselink/issues/2301)
@Entity
@Table(name = EntityFloat.TABLE_NAME)
public class EntityFloat {

    static final String TABLE_NAME = "ADV_ENTITY_FLOAT";

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

    // Based on reproduction scenario from issue #2301 (https://github.com/eclipse-ee4j/eclipselink/issues/2301)
    public static final class Populator extends TogglingFastTableCreator {

        public static EntityFloat[] ENTITY_FLOAT = new EntityFloat[] {
                // Tallest and smallest length
                new EntityFloat(70071, 17.0f, 17.1f, 7.7f, "testOLGH28289#70071"),
                // Tallest and largest length
                new EntityFloat(70077, 77.0f, 17.7f, 7.7f, "testOLGH28289#70077"),
                new EntityFloat(70007, 70.0f, 10.7f, 0.7f, "testOLGH28289#70007")
        };

        public static void populate(Session session) {
            List<Object> entities = Arrays.asList(ENTITY_FLOAT);
            UnitOfWork unitOfWork = session.acquireUnitOfWork();
            unitOfWork.removeAllReadOnlyClasses();
            unitOfWork.registerAllObjects(entities);
            unitOfWork.commit();
        }

        // Supported data types according to https://docs.oracle.com/cd/E19501-01/819-3659/gcmaz/
        public static TableDefinition buildTable() {
            TableDefinition table = new TableDefinition();
            table.setName(TABLE_NAME);
            table.addField(createNumericPk("ID", 10));
            table.addField(createFloatColumn("HEIGHT", false));
            table.addField(createFloatColumn("LENGTH", false));
            table.addField(createFloatColumn("WIDTH", false));
            table.addField(createStringColumn("DESCRIPTION", 255,false));
            return table;
        }

        private Populator() {
            throw new UnsupportedOperationException("No instances of EntityFloatPopulator are allowed");
        }

    }

}
