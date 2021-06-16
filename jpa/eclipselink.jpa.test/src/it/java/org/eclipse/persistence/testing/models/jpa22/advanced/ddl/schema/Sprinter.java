/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl.schema;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

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
