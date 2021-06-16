/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     IBM - Bug 573435: CriteriaBuilder construct throws argument type mismatch exception with Case Select
package org.eclipse.persistence.jpa.test.query.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EntityTbl01.class)
public class EntityTbl01_ {
    public static volatile SingularAttribute<EntityTbl01, Long> KeyString;
    public static volatile SingularAttribute<EntityTbl01, String> itemString1;
    public static volatile SingularAttribute<EntityTbl01, String> itemString2;
    public static volatile SingularAttribute<EntityTbl01, String> itemString3;
    public static volatile SingularAttribute<EntityTbl01, String> itemString4;
    public static volatile SingularAttribute<EntityTbl01, Integer> itemInteger1;
}
