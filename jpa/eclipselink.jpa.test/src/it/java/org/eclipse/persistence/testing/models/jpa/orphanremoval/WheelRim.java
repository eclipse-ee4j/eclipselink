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
//     05/1/2009-2.0 Guy Pelletier/David Minsky
//       - 249033: JPA 2.0 Orphan removal
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="OR_WheelRim")
@Table(name="JPA_OR_WHEEL_RIM")
public class WheelRim {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_OR_WHEEL_RIM_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_OR_WHEEL_RIM_TABLE_GENERATOR",
        table="JPA_ORPHAN_REMOVAL_SEQUENCE",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WHEEL_RIM_SEQ"
    )
    protected int id;

    public WheelRim() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "WheelRim ["+ id +"]";
    }
}
