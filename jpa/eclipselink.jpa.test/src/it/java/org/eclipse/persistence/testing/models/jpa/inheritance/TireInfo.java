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
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.io.*;
import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.*;
import static jakarta.persistence.InheritanceType.*;

@Entity
@Table(name="CMP3_TIRE")
@Inheritance(strategy=SINGLE_TABLE)
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
