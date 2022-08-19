/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     02/01/2022: Tomas Kraus
//       - Issue 1442: Implement New Jakarta Persistence 3.1 Features
package org.eclipse.persistence.jpa.test.criteria.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * JPA Entity used in {@code CriteriaBuilder} math functions tests.
 */
@Entity
public class NumberEntity {

    @Id
    private Integer id;

    private Long longValue;

    private Float floatValue;

    private Double doubleValue;

    @Column(precision=15, scale=10)
    private BigDecimal bdValue;

    public NumberEntity() {
    }

    public NumberEntity(final Integer id, final Long longValue,
                        final Float floatValue, final Double doubleValue, final BigDecimal bdValue) {
        this.setId(id);
        this.setLongValue(longValue);
        this.setFloatValue(floatValue);
        this.setDoubleValue(doubleValue);
        this.setBdValue(bdValue);
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(final Long longValue) {
        this.longValue = longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(final Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(final Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public BigDecimal getBdValue() {
        return bdValue;
    }

    public void setBdValue(BigDecimal bdValue) {
        this.bdValue = bdValue;
    }

}
