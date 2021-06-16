/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     03/19/2018-2.7.2 Lukas Jungmann
//       - 413120: Nested Embeddable Null pointer
//       - 496836: NullPointerException on ObjectChangeSet.mergeObjectChanges
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

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
