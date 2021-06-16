/*
 * Copyright (c) 2013, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl.schema;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="JPA21_DDL_SPRINTER", schema="RUNNER")
@PrimaryKeyJoinColumn(
    name="SPRINTER_ID",
    referencedColumnName="ID",
    foreignKey=@ForeignKey(
        name="Sprinter_Foreign_Key",
        foreignKeyDefinition="FOREIGN KEY (SPRINTER_ID) REFERENCES JPA21_DDL_RUNNER (ID)"
    )
)
public class Sprinter extends Runner {

    public Sprinter() {}

}
