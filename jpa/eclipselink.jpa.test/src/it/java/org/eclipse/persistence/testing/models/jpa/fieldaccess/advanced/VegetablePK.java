/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;
import jakarta.persistence.Embeddable;

@Embeddable
public class VegetablePK implements Serializable {
    private String name;
    private String color;

    public VegetablePK() {}

    public VegetablePK(String name, String color) {
        setName(name);
        setColor(color);
    }

    public boolean equals(Object otherVegetablePK) {
        if (otherVegetablePK instanceof VegetablePK) {
            if (! getName().equals(((VegetablePK) otherVegetablePK).getName())) {
                return false;
            }

            return ( getColor().equals(((VegetablePK) otherVegetablePK).getColor()));
        }

        return false;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int hashCode() {
        int hash = 0;
        hash += (this.getName() != null ? this.getName().hashCode() : 0);
        hash += (this.getColor() != null ? this.getColor().hashCode() : 0);
        return hash;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "VegetablePK [id=" + getName() + " - " + getColor() + "]";
    }
}
