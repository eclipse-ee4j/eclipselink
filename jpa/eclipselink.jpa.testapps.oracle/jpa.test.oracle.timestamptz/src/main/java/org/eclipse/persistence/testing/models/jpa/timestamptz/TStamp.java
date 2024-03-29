/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.timestamptz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.TypeConverter;

import java.io.Serializable;
import java.util.Calendar;


@Entity
@Table(name="TIME_STAMP")
public class TStamp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column
    @Convert(value="TimeStameNoTZ")
    @TypeConverter(name="TimeStameNoTZ", dataType=TIMESTAMPTZ.class)
    private Calendar noZone;

    @Column
    @Convert(value="TimeStameTZ")
    @TypeConverter(name="TimeStameTZ", dataType=TIMESTAMPTZ.class)
    private Calendar tsTZ;

    @Column
    @Convert(value="TimeStameLTZ")
    @TypeConverter(name="TimeStameLTZ", dataType=TIMESTAMPLTZ.class)
    private Calendar tsLTZ;

    public TStamp() {}

    public Integer getId() {
        return this.id;
    }

    public Calendar getNoZone() {
        return this.noZone;
    }

    public void setNoZone(Calendar noZone) {
        this.noZone = noZone;
    }

    public Calendar getTsTZ() {
        return this.tsTZ;
    }

    public void setTsTZ(Calendar tsTZ) {
        this.tsTZ = tsTZ;
    }

    public Calendar getTsLTZ() {
        return this.tsLTZ;
    }

    public void setTsLTZ(Calendar tsLTZ) {
        this.tsLTZ = tsLTZ;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}

