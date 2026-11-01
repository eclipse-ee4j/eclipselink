/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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

import jakarta.persistence.metamodel.BooleanAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(BooleanCriteriaRecord.class)
public class BooleanCriteriaRecord_ {
    public static volatile SingularAttribute<BooleanCriteriaRecord, Integer> id;
    public static volatile BooleanAttribute<BooleanCriteriaRecord> leftValue;
    public static volatile BooleanAttribute<BooleanCriteriaRecord> rightValue;
    public static volatile BooleanAttribute<BooleanCriteriaRecord> primitiveValue;
    public static volatile BooleanAttribute<BooleanCriteriaRecord> convertedValue;
    public static volatile BooleanAttribute<BooleanCriteriaRecord> numericConvertedValue;
    public static volatile SingularAttribute<BooleanCriteriaRecord, BooleanCriteriaGroup> group;
}
