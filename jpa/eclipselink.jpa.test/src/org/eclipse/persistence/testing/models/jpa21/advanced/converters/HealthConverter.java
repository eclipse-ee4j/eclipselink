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
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;

@Converter(autoApply=false)
public class HealthConverter implements AttributeConverter<Health, String> {

    @Override
    public String convertToDatabaseColumn(Health health) {
        if (health == null) {
            return null;
        } else if (health.equals(Health.H)) {
            return Health.HEALTHY.name();
        } else if (health.equals(Health.I)) {
            return Health.INJURED.name();
        } else if (health.equals(Health.S)) {
            return Health.SICK.name();
        } else {
            return health.name();
        }
    }

    @Override
    public Health convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : Health.valueOf(dbData);
    }
}

