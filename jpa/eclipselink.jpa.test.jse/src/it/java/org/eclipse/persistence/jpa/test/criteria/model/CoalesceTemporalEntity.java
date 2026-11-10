/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
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

import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class CoalesceTemporalEntity {
    @Id
    private Integer id;

    @Temporal(TemporalType.DATE)
    private Date dateValue;

    @Temporal(TemporalType.TIME)
    private Date timeValue;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestampValue;

    @Convert(converter = IdentityTimestampConverter.class)
    private Date identityValue;

    @Convert(converter = ShiftedTimestampConverter.class)
    private Date shiftedValue;

    @Convert(converter = NormalizingTimestampConverter.class)
    private Date normalizedValue;

    public CoalesceTemporalEntity() {
    }

    public CoalesceTemporalEntity(Integer id) {
        this.id = id;
    }

    @Converter
    public static class IdentityTimestampConverter implements AttributeConverter<Date, Timestamp> {
        @Override
        public Timestamp convertToDatabaseColumn(Date value) {
            return value == null ? null : new Timestamp(value.getTime());
        }

        @Override
        public Date convertToEntityAttribute(Timestamp value) {
            return value;
        }
    }

    @Converter
    public static class ShiftedTimestampConverter implements AttributeConverter<Date, Timestamp> {
        @Override
        public Timestamp convertToDatabaseColumn(Date value) {
            return value == null ? null : new Timestamp(value.getTime() + 86_400_000L);
        }

        @Override
        public Date convertToEntityAttribute(Timestamp value) {
            return value == null ? null : new Date(value.getTime() - 86_400_000L);
        }
    }

    @Converter
    public static class NormalizingTimestampConverter implements AttributeConverter<Date, Timestamp> {
        @Override
        public Timestamp convertToDatabaseColumn(Date value) {
            if (value == null) {
                return null;
            }
            value.setTime(value.getTime() / 1000 * 1000);
            return new Timestamp(value.getTime());
        }

        @Override
        public Date convertToEntityAttribute(Timestamp value) {
            return value;
        }
    }
}
