/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
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
//     04/11/2018 - Will Dazey
//       - 533148 : Add the eclipselink.jpa.sql-call-deferral property
package org.eclipse.persistence.jpa.test.property.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "PARENT")
@PrimaryKeyJoinColumn(name="PARENT_PK", referencedColumnName="ABSTRACTPARENT_PK")
public class Parent extends AbstractParent {
}
