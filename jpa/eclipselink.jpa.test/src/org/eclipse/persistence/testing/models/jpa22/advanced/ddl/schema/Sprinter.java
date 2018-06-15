/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl.schema;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="JPA22_DDL_SPRINTER", schema="RUNNER")
@PrimaryKeyJoinColumn(
    name="SPRINTER_ID",
    referencedColumnName="ID",
    foreignKey=@ForeignKey(
        name="Sprinter_Foreign_Key",
        foreignKeyDefinition="FOREIGN KEY (SPRINTER_ID) REFERENCES JPA22_DDL_RUNNER (ID)"
    )
)
public class Sprinter extends Runner {

    public Sprinter() {}

}
