/*
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     05/04/2022 - Will Dazey
//       - Add support for partial parameter binding for DB2
package org.eclipse.persistence.jpa.test.query.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(QuerySyntaxEntity.class)
public class QuerySyntaxEntity_ {
    public static volatile SingularAttribute<QuerySyntaxEntity, Long> id;
    public static volatile SingularAttribute<QuerySyntaxEntity, String> strVal1;
    public static volatile SingularAttribute<QuerySyntaxEntity, String> strVal2;
    public static volatile SingularAttribute<QuerySyntaxEntity, Integer> intVal1;
    public static volatile SingularAttribute<QuerySyntaxEntity, Integer> intVal2;
    public static volatile CollectionAttribute<QuerySyntaxEntity, String> colVal1;
}
