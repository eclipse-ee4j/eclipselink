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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import java.io.Serializable;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.EmbeddedId;

@Entity
@Table(name="CMP3_COUNTRY_DWELLER")
public class CountryDweller implements Serializable {
    private int age;
    private Name name;
    private World world;
    private String gender;

    public CountryDweller () {}

    @Column(name="AGE")
    public int getAge() {
        return age;
    }

    @Column(name="GENDER")
    public String getGender() {
        return gender;
    }

    @EmbeddedId
    public Name getName() {
        return name;
    }

    @ManyToOne
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
