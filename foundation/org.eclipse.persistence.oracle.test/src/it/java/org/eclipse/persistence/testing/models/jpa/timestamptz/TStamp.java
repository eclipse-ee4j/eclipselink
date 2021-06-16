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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.timestamptz;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Calendar;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TIMESTAMPLTZ;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.Convert;


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

