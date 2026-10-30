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
@Table(name = "JPA_SC_RECORD")
public class ScalarCriteriaRecord {
    @Id
    public Integer id;

    public Integer amount;
    public Double fraction;

    @Convert(converter = ScaledIntegerConverter.class)
    @Column(name = "CONVERTED_AMOUNT")
    public Integer convertedAmount;

    @Convert(converter = ScaledIntegerConverter.class)
    @Column(name = "PRIMITIVE_CONVERTED_AMOUNT")
    public int primitiveConvertedAmount;

    @Column(name = "TEXT_VALUE")
    public String text;

    @Column(name = "NULLABLE_TEXT")
    public String nullableText;

    @Convert(converter = PrefixConverter.class)
    @Column(name = "CONVERTED_TEXT")
    public String convertedText;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    public ScalarCriteriaGroup group;

    public ScalarCriteriaRecord() {
    }

    public ScalarCriteriaRecord(Integer id, Integer amount, Double fraction, String text,
                                String nullableText, ScalarCriteriaGroup group) {
        this.id = id;
        this.amount = amount;
        this.convertedAmount = amount;
        this.primitiveConvertedAmount = amount == null ? 0 : amount;
        this.fraction = fraction;
        this.text = text;
        this.nullableText = nullableText;
        this.convertedText = "code" + id;
        this.group = group;
    }

    @Converter
    public static class PrefixConverter implements AttributeConverter<String, String> {
        @Override
        public String convertToDatabaseColumn(String value) {
            return value == null ? null : "stored:" + value;
        }

        @Override
        public String convertToEntityAttribute(String value) {
            return value == null ? null : value.substring("stored:".length());
        }
    }

    @Converter
    public static class ScaledIntegerConverter implements AttributeConverter<Integer, Long> {
        @Override
        public Long convertToDatabaseColumn(Integer value) {
            return value == null ? null : value.longValue() * 10;
        }

        @Override
        public Integer convertToEntityAttribute(Long value) {
            return value == null ? null : (int) (value / 10);
        }
    }
}
