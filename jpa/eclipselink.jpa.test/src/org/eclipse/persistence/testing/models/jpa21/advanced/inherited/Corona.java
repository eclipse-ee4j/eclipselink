/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
package org.eclipse.persistence.testing.models.jpa21.advanced.inherited;

import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name="JPA21_CORONA")
public class Corona extends Beer<Integer, Double, Corona> implements Cloneable {
    public Corona() {}

    public Corona clone() throws CloneNotSupportedException {
        return (Corona) super.clone();
    }

    public boolean equals(Object anotherCorona) {
        if (anotherCorona.getClass() != Corona.class) {
            return false;
        }

        return (getId().equals(((Corona)anotherCorona).getId()));
    }
}
