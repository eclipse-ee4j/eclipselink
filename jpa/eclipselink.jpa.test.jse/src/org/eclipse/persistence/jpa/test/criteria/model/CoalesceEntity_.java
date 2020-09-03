/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.criteria.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CoalesceEntity.class)
public class CoalesceEntity_ {
    public static volatile SingularAttribute<CoalesceEntity, Integer> id;
    public static volatile SingularAttribute<CoalesceEntity, String> description;
    public static volatile SingularAttribute<CoalesceEntity, java.math.BigDecimal> bigDecimal;
    public static volatile SingularAttribute<CoalesceEntity, java.util.Date> date;
}
