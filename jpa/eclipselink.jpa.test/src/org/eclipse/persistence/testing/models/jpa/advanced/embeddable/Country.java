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
//     03/19/2018-2.7.2 Lukas Jungmann
//       - 413120: Nested Embeddable Null pointer
//       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * Country instance to be {@link Embedded} into other entities.
 */
@Embeddable
public class Country implements java.io.Serializable {

    /** Country code. */
    private String code;

    /** Country name. */
    private String country;

    /** Continent. */
    private Continent continent;

    /**
     * Constructs an empty instance of <code>Country</code> class.
     */
    public Country() {}

    /**
     * Constructs an instance of <code>Country</code> class with country and code set.
     * @param code    Country code.
     * @param country Country name.
     */
    public Country(String code, String country) {
        this(code, country, (Continent) null);
    }

    /**
     * Constructs an instance of <code>Country</code> class with country and code set.
     * @param code    Country code.
     * @param country Country name.
     */
    public Country(String code, String country, String continent) {
        this(code, country, new Continent(continent));
    }

    /**
     * Constructs an instance of <code>Country</code> class with country and code set.
     * @param code    Country code.
     * @param country Country name.
     */
    public Country(String code, String country, Continent continent) {
        this.code = code;
        this.country = country;
        this.continent = continent;
    }

    /**
     * Get country code.
     * @return Country code.
     */
    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    /**
     * Set country code.
     * @param code Country code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get country name.
     * @return Country name.
     */
    @Basic
    @Column(name = "COUNTRY")
    public String getCountry() {
        return country;
    }

    /**
     * Set country name.
     * @param name Country name.
     */
    public void setCountry(String name) {
        country = name;
    }

    @Embedded
    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    /**
     * Compare this instance with given object.
     * @return Value of <code>true</code> if given object is an instance of the same class
     *         and values of both attributes are equal. Otherwise returns value of <code>false</code>.
     */
    public boolean equals(Object object) {
        Country other = (object instanceof Country) ? (Country) object : null;
        // Given object is not an instance of the same class.
        if (other == null) {
            return false;
        }
        // Compare code.
        if (code != null) {
            if (!code.equals(other.code)) {
                return false;
            }
        } else if (other.code != null) {
            return false;
        }
        // Compare name
        if (country != null) {
            if (!country.equals(other.country)) {
                return false;
            }
        } else if (other.country != null) {
            return false;
        }
        // Compare continent
        if (continent != null) {
            if (!continent.equals(other.continent)) {
                return false;
            }
        } else if (other.continent != null) {
            return false;
        }
        return true;
    }

    /**
     * Constructs {@link String} representation of this entity instance.
     * @return {@link String} representation of this entity instance.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName() + "[");
        result.append("country: " + getCountry());
        result.append(", code: " + getCode());
        result.append(", continent: " + getContinent());
        result.append("]");
        return result.toString();
    }
}
