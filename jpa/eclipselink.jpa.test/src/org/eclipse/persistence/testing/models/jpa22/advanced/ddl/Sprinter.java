/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="JPA22_DDL_SPRINTER")
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
