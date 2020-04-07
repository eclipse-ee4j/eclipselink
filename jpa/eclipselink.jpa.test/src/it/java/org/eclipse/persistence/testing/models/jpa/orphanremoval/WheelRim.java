/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
