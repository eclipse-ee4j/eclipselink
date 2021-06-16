/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/18/2010-2.2 Guy Pelletier
//       - 300458: EclispeLink should throw a more specific exception than NPE
//     07/16/2010-2.2 Guy Pelletier
//       - 260296: mixed access with no Transient annotation does not result in error
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="JPA_NOISY")
@Access(AccessType.FIELD)
public class NoiseBylaw extends Bylaw {
    // The access type is FIELD. If we map this map instead of its associated
    // methods marked as access PROPERTY, we will map the wrong column name
    // and tests from InheritedModelJunitTest.
    public String description;

    @Access(AccessType.PROPERTY)
    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
