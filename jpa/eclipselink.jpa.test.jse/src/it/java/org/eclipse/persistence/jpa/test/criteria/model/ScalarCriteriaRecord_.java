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

import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.persistence.metamodel.TextAttribute;

@StaticMetamodel(ScalarCriteriaRecord.class)
public class ScalarCriteriaRecord_ {
    public static volatile NumericAttribute<ScalarCriteriaRecord, Integer> id;
    public static volatile NumericAttribute<ScalarCriteriaRecord, Integer> amount;
    public static volatile NumericAttribute<ScalarCriteriaRecord, Integer> convertedAmount;
    public static volatile NumericAttribute<ScalarCriteriaRecord, Integer> primitiveConvertedAmount;
    public static volatile NumericAttribute<ScalarCriteriaRecord, Double> fraction;
    public static volatile TextAttribute<ScalarCriteriaRecord> text;
    public static volatile TextAttribute<ScalarCriteriaRecord> nullableText;
    public static volatile TextAttribute<ScalarCriteriaRecord> convertedText;
    public static volatile SingularAttribute<ScalarCriteriaRecord, ScalarCriteriaGroup> group;
}
