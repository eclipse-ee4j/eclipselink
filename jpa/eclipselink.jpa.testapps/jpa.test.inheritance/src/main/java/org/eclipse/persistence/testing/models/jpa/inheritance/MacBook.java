/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     12/18/2009-2.1 Guy Pelletier
//       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@NamedQuery(
    name="findAllMacBooks",
    query="SELECT mb FROM MacBook mb"
)
@Table(name="JPA_MACBOOK")
public class MacBook extends Apple {
    public Integer ram;

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        if (ram > 4) {
            this.ram = 4;
        } else {
            this.ram = ram;
        }
    }
}
