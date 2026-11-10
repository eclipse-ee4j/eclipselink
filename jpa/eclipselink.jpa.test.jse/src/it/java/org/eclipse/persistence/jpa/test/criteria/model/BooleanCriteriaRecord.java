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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_BC_RECORD")
public class BooleanCriteriaRecord {
    @Id
    public Integer id;

    @Column(name = "FLAG_LEFT")
    public Boolean leftValue;

    @Column(name = "FLAG_RIGHT")
    public Boolean rightValue;

    @Column(name = "FLAG_PRIMITIVE")
    public boolean primitiveValue;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "FLAG_ENCODED", length = 1)
    public Boolean convertedValue;

    @Convert(converter = IntegerBooleanConverter.class)
    @Column(name = "FLAG_NUMERIC")
    public Boolean numericConvertedValue;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    public BooleanCriteriaGroup group;

    public BooleanCriteriaRecord() {
    }

    public BooleanCriteriaRecord(int id, Boolean left, Boolean right, BooleanCriteriaGroup group) {
        this.id = id;
        leftValue = left;
        rightValue = right;
        primitiveValue = Boolean.TRUE.equals(left);
        convertedValue = left;
        numericConvertedValue = left;
        this.group = group;
    }

    @Converter
    public static class YesNoConverter implements AttributeConverter<Boolean, String> {
        @Override
        public String convertToDatabaseColumn(Boolean value) {
            return value == null ? null : value ? "Y" : "N";
        }

        @Override
        public Boolean convertToEntityAttribute(String value) {
            return value == null ? null : value.equals("Y");
        }
    }

    @Converter
    public static class IntegerBooleanConverter implements AttributeConverter<Boolean, Integer> {
        @Override
        public Integer convertToDatabaseColumn(Boolean value) {
            return value == null ? null : value ? 7 : 3;
        }

        @Override
        public Boolean convertToEntityAttribute(Integer value) {
            return value == null ? null : value == 7;
        }
    }
}
