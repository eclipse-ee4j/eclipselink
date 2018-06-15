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
package org.eclipse.persistence.testing.models.jpa22.advanced.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Level;

@Converter(autoApply=false)
public class LevelConverter implements AttributeConverter<Level, String> {

    @Override
    public String convertToDatabaseColumn(Level level) {
        if (level == null) {
            return null;
        } else if (level.equals(Level.A)) {
            return Level.AMATEUR.name();
        } else if (level.equals(Level.E)) {
            return Level.ELITE.name();
        } else {
            return level.name();
        }
    }

    @Override
    public Level convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : Level.valueOf(dbData);
    }
}

