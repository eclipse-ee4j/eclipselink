/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/19/09 dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.*;

@Entity(name="PO_WheelNut")
@Table(name="CMP3_PO_WHEEL_NUT")
public class WheelNut {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_WHEEL_NUT_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_WHEEL_NUT_TABLE_GENERATOR",
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WHEEL_NUT_SEQ"
    )
    protected int id;

    @ManyToOne
    protected Wheel wheel;

    public WheelNut() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Wheel getWheel() {
        return wheel;
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }

}
