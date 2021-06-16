/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa22.advanced.xml;

public class Responsibility {
    public Long uniqueIdentifier;
    public String description;

    public Responsibility() {}

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Responsibility) {
            Responsibility r = (Responsibility) obj;

            return (r.getDescription().equals(getDescription()) && (r.getUniqueIdentifier().equals(getUniqueIdentifier())));
        }

        return false;
    }

    public String getDescription() {
        return description;
    }

    public Long getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    @Override
    public int hashCode() {
        if (uniqueIdentifier != null) {
            return uniqueIdentifier.intValue();
        }

        return -1;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUniqueIdentifier(Long uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }
}
