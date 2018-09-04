/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

//     09/04/2018-3.0 Ravi Babu Tummuru
//       - 538183: SETTING QUERYHINTS.CURSOR ON A NAMEDQUERY THROWS QUERYEXCEPTION

package org.eclipse.persistence.testing.models.jpa.advanced;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

@Entity
@Table(name="RBT_MYTESTENTITY") 
@NamedNativeQueries( @NamedNativeQuery(name="allTestEntitiesAnnotated", query="SELECT ID FROM RBT_MYTESTENTITY"
    /* , hints={@QueryHint(name=QueryHints.CURSOR, value="true")}) */ ) )
public class MyTestEntity {
    @Id
    public Long id;
}
