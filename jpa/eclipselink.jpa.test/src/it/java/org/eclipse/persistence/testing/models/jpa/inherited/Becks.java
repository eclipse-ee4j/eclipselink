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
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table(name="CMP3_BECKS")
public class Becks extends Beer<Integer, Double, Becks> implements Cloneable {
    public Becks() {}

    public Becks clone() throws CloneNotSupportedException {
        return (Becks) super.clone();
    }

    public boolean equals(Object anotherBecks) {
        if (anotherBecks.getClass() != Becks.class) {
            return false;
        }

        return (getId().equals(((Becks)anotherBecks).getId()));
    }
}
