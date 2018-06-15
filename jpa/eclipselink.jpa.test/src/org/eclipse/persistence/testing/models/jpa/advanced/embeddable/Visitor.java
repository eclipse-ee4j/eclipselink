/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - Initial API and implementation.
//     07/07/2014-2.6 Tomas Kraus
//       - 439127: Modified to use this class in jUnit test model.
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import javax.persistence.*;
import java.util.*;

/**
 * Visitor entity with Embedded attribute and SqlResultSetMapping.
 */
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "VisitorResults", entities = {
        @EntityResult(entityClass = org.eclipse.persistence.testing.models.jpa.advanced.embeddable.Visitor.class, fields = {
            @FieldResult(name = "id", column = "OID"),
            @FieldResult(name = "name", column = "ONAME"),
            @FieldResult(name = "country.country", column = "OCOUNTRY"),
            @FieldResult(name = "country.code", column = "OCODE")
        })
    }),
})
@Entity
@Table(name = "CMP3_EMBED_VISITOR")
public class Visitor implements java.io.Serializable {

    // Instance variables
    /** Entity primary key. */
    private String id;

    /** Visitor's name. */
    private String name;

    /** Visitor's country. */
    private Country country;

    /**
     * Constructs an empty instance of <code>Visitor</code> entity.
     */
    public Visitor() {}

    /**
     * Constructs an instance of <code>Visitor</code> entity with primary key and name set.
     * @param id   Entity primary key.
     * @param name Visitor's name.
     */
    public Visitor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructs an instance of <code>Visitor</code> entity with primary key, name and country set.
     * @param id      Entity primary key.
     * @param name    Visitor's name.
     * @param country Visitor's country.
     */
    public Visitor(String id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    /**
     * Get entity primary key.
     * @return Entity primary key.
     */
    @Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    /**
     * Set entity primary key.
     * @param id Entity primary key.
     */
    public void setId(String v) {
        this.id = v;
    }

    /**
     * Get visitor's name.
     * @return Visitor's name.
     */
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    /**
     * Set visitor's name.
     * @param name Visitor's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get visitor's country.
     * @return Visitor's country.
     */
    @Embedded
    public Country getCountry() {
        return country;
    }

    /**
     * Set visitor's country.
     * @param country Visitor's country.
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Compare this entity with given object.
     * @return Value of <code>true</code> if given object is an instance of the same class
     *         and values of ID attributes are equal. Otherwise returns value of <code>false</code>.
     */
    public boolean equals(Object object) {
        Visitor other = (object instanceof Visitor) ? (Visitor) object : null;
        // Given object is not an instance of the same class.
        if (other == null) {
            return false;
        }
        // Compare id.
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        } else if (other.id != null) {
            return false;
        }
        return true;
    }

    /**
     * Generates entity hash code.
     * @return Entity hash code based on primary key value.
     */
    public int hashCode() {
        return id != null ? id.hashCode() : "null".hashCode();
    }

    /**
     * Constructs {@link String} representation of this entity instance.
     * @return {@link String} representation of this entity instance.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName() + "[");
        result.append("id: " + getId());
        result.append(",  name: " + getName());
        if (getCountry() != null) {
            result.append(", country: " + getCountry());
        } else {
            result.append(", country: null");
        }
        result.append("]");
        return result.toString();
    }
}
