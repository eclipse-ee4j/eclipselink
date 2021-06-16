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
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.JOINED;

import org.eclipse.persistence.annotations.FetchAttribute;
import org.eclipse.persistence.annotations.FetchGroup;

@Entity
@Table(name="JPA_HOCKEY_GEAR")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="GEAR_TYPE")
@DiscriminatorValue("HOG")
@FetchGroup(name="MSRP", attributes={@FetchAttribute(name="msrp")})
public abstract class HockeyGear extends Gear {
    @Basic
    @Column(name="MSRP")
    public Double msrp;

    @Column(name="DESCRIP")
    public String description;

    @Id
    @GeneratedValue(strategy=TABLE, generator="HOCKEY_GEAR_TABLE_GENERATOR")
    @TableGenerator(
        name="HOCKEY_GEAR_TABLE_GENERATOR",
        table="JPA_HOCKEY_GEAR_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="HG_SEQ",
        initialValue=50
    )
    @Column(name="SERIAL_NUMBER")
    public Integer serialNumber;

    public HockeyGear() {}

    public String getDescription() {
        return description;
    }

    public Double getMsrp() {
        return msrp;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMsrp(Double msrp) {
        this.msrp = msrp;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
}
