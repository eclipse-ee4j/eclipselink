/*
 * Copyright (c) 2008, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     09/10/2008-2.4.1 Daryl Davis
//       - 386939: @ManyToMany Map<Entity,Entity> unidirectional reverses Key and Value fields on Update
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ValueEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="VALUE_ENTITY_ID")
    public String id;
    public String name;

    public ValueEntity () {
    }

    public ValueEntity (String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Value Entity [" + id + ", " + name + "]";
    }
}
