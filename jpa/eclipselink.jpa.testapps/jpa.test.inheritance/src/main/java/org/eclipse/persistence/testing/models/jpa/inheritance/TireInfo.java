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
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Transient;

import java.io.Serializable;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

/**
 * Note Y is currently not used, but was added as a test for bug 336133.  Please do not remove,
 * but feel free to add meaning to Y.
 */
@Entity
@Table(name="CMP3_TIRE")
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorColumn(name="TIRE_TYPE")
@DiscriminatorValue("Normal")
public class TireInfo<Y> extends TireInfoMappedSuperclass implements Serializable {
    protected Integer id;

    public TireInfo() {}

    @Transient
    private Y variable;

    @Id
    @GeneratedValue(strategy=TABLE, generator="TIRE_TABLE_GENERATOR")
    @TableGenerator(
        name="TIRE_TABLE_GENERATOR",
        table="CMP3_INHERITANCE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="TIRE_SEQ")
    @Column(name="ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
