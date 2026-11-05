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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.persistence.metamodel.TemporalAttribute;
import jakarta.persistence.metamodel.TextAttribute;

@StaticMetamodel(TemporalCriteriaRecord.class)
public class TemporalCriteriaRecord_ {
    public static volatile NumericAttribute<TemporalCriteriaRecord, Integer> id;
    public static volatile TemporalAttribute<TemporalCriteriaRecord, LocalDate> dateValue;
    public static volatile TemporalAttribute<TemporalCriteriaRecord, LocalTime> timeValue;
    public static volatile TemporalAttribute<TemporalCriteriaRecord, LocalTime> convertedTime;
    public static volatile TemporalAttribute<TemporalCriteriaRecord, LocalDateTime> stampValue;
    public static volatile TextAttribute<TemporalCriteriaRecord> text;
    public static volatile NumericAttribute<TemporalCriteriaRecord, Integer> amount;
    public static volatile SingularAttribute<TemporalCriteriaRecord, TemporalCriteriaRecord> parent;
}
