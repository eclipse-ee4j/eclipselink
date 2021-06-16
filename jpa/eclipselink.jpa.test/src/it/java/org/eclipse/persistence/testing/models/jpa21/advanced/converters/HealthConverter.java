/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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

