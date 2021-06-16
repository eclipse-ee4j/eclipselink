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

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;

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

