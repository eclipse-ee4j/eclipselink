/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
//     IBM - Bug 573435: CriteriaBuilder construct throws argument type mismatch exception with Case Select
package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EntityTbl01.class)
public class EntityTbl01_ {
    public static volatile SingularAttribute<EntityTbl01, Long> KeyString;
    public static volatile SingularAttribute<EntityTbl01, String> itemString1;
    public static volatile SingularAttribute<EntityTbl01, String> itemString2;
    public static volatile SingularAttribute<EntityTbl01, String> itemString3;
    public static volatile SingularAttribute<EntityTbl01, String> itemString4;
    public static volatile SingularAttribute<EntityTbl01, Integer> itemInteger1;
}