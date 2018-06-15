/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=false)
public class DateConverter implements AttributeConverter<Date, Long> {

    @Override
    public Long convertToDatabaseColumn(Date date) {
        return date.getTime();
    }

    @Override
    public Date convertToEntityAttribute(Long dbData) {
        return new Date(dbData);
    }
}
