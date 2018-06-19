/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/20/2009-1.1 Guy Pelletier
//       - 259829: TABLE_PER_CLASS with abstract classes does not work
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverride(name="id", column = @Column(name="SMALL_ID"))
@Table(name="TPC_SMALL_ASSIGNMENT")
public class SmallAssignment extends Assignment {

}
