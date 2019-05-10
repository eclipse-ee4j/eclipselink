/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import java.io.*;
import javax.persistence.*;

@Entity
@Table(name="JPA21_TIRE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TIRE_TYPE")
@DiscriminatorValue("Normal")
/**
 * Note Y is currently not used, but was added as a test for bug 336133.  Please do not remove,
 * but feel free to add meaning to Y.
 */
public class TireInfo<Y> extends TireInfoMappedSuperclass implements Serializable {
    protected Integer id;

    public TireInfo() {}

    @Transient
    private Y variable;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="TIRE_TABLE_GENERATOR")
    @TableGenerator(
        name="TIRE_TABLE_GENERATOR",
        table="JPA21_INHERITANCE_SEQ",
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
