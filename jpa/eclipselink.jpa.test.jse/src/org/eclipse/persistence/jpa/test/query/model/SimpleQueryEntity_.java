/*
 * Copyright (c) 2021 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
//     IBM - Bug 521402: Add support for Criteria queries with only literals
package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SimpleQueryEntity.class)
public class SimpleQueryEntity_ {
    public static volatile SingularAttribute<SimpleQueryEntity, Long> id;
    public static volatile SingularAttribute<SimpleQueryEntity, Integer> intVal1;
    public static volatile SingularAttribute<SimpleQueryEntity, String> strVal1;
}