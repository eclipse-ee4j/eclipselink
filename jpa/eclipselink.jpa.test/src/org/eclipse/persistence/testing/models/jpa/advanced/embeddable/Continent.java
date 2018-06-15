/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/19/2018-2.7.2 Lukas Jungmann
//       - 413120: Nested Embeddable Null pointer
//       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Continent implements Serializable {

    private String continentCode;
    
    private Description description;

    public Continent() {
    }

    public Continent(String continentCode) {
        this.continentCode = continentCode;
    }

    @Column(name = "CONTINENT")
    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    @Embedded
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((continentCode == null) ? 0 : continentCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Continent other = (Continent) obj;
        if (continentCode == null) {
            if (other.continentCode != null)
                return false;
        } else if (!continentCode.equals(other.continentCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Continent [continent=" + continentCode + "]";
    }

    
}
