/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/24/2017-3.0 Tomas Kraus
//       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
package org.eclipse.persistence.testing.models.jpa22.jta;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Animal entity.
 */
@Entity
@Table(name="JPA22_CDI_ANIMAL")
public class Animal implements Cloneable {

    /** Entity primary key. */
    @Id
    @GeneratedValue
    private Integer id;

    /** Animal name (just to have something to set/modify). */
    private String name;

    /**
     * Creates an instance of animal entity.
     */
    public Animal() {
    }

    /**
     * Creates an instance of animal entity with all attributes set.
     *
     * @param id entity primary key
     * @param name animal name
     */
    private Animal(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get entity primary key.
     *
     * @return entity primary key
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set entity primary key.
     *
     * @param id entity primary key
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Get animal's name.
     *
     * @return animal's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set animal's name.
     *
     * @param name animal's name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Return human readable representation of animal entity.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(Integer.toString(id));
        sb.append(" name: \"").append(name).append('\"');
        return sb.toString();
    }

    /**
     * Clone animal entity.
     * All attributes are immutable so their original references are used in new instance.
     */
    @Override
    public Animal clone() {
        return new Animal(id, name);
    }

}
