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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "JPA_TEMPORAL_CRITERIA")
public class TemporalCriteriaRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    public Integer id;

    @Column(name = "R_DATE")
    public LocalDate dateValue;

    @Column(name = "R_TIME")
    public LocalTime timeValue;

    @Column(name = "R_STAMP", secondPrecision = 6)
    public LocalDateTime stampValue;

    @Column(name = "R_TIME_TEXT")
    @Convert(converter = ClockText.class)
    public LocalTime convertedTime;

    @Converter
    public static class ClockText implements AttributeConverter<LocalTime, String> {
        @Override
        public String convertToDatabaseColumn(LocalTime value) {
            return value == null ? null : value.toString();
        }

        @Override
        public LocalTime convertToEntityAttribute(String value) {
            return value == null ? null : LocalTime.parse(value);
        }
    }

    @Column(name = "R_TEXT")
    public String text;

    @Column(name = "R_AMOUNT")
    public Integer amount;

    @ManyToOne
    @JoinColumn(name = "R_PARENT")
    public TemporalCriteriaRecord parent;

    public TemporalCriteriaRecord() {
    }

    public TemporalCriteriaRecord(int id, LocalDateTime stamp, String text, Integer amount) {
        this.id = id;
        stampValue = stamp;
        dateValue = stamp == null ? null : stamp.toLocalDate();
        timeValue = stamp == null ? null : stamp.toLocalTime().withNano(0);
        convertedTime = timeValue;
        this.text = text;
        this.amount = amount;
    }
}
