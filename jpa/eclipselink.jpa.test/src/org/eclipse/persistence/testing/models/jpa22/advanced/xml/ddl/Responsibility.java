/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl;

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
