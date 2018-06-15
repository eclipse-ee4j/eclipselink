/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     04/02/2008-1.0M6 Guy Pelletier
//       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
//     08/27/2008-1.1 Guy Pelletier
//       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.io.Serializable;

public class CountryDweller implements Serializable {
    private int age;
    private Name name;
    private World world;
    private String gender;

    public CountryDweller () {}

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public Name getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public String toString() {
        return "CountryDweller: " + getName().toString();
    }
}
