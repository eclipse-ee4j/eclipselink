/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.timestamptz;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

