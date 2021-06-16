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
//     10/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     02/28/2013-2.5 Chris Delahunt
//       - 402029: Application exceptions need to be wrapped in PersistenceException
package org.eclipse.persistence.testing.models.jpa22.advanced.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply=false)
public class ResponsibilityConverter implements AttributeConverter<String, String> {
    public static String THROW_EXCEPTION_IN_TO_DATABASE_COLUMN = "throwExceptionInToDBColumn";
    public static String THROW_EXCEPTION_IN_TO_ENTITY_ATTRIBUTE = "throwExceptionInToEntityAttribute";

    @Override
    public String convertToDatabaseColumn(String responsibility) {
        if (responsibility == null) {
            return null;
        } else if (THROW_EXCEPTION_IN_TO_DATABASE_COLUMN.equals(responsibility)) {
            throw new RuntimeException(THROW_EXCEPTION_IN_TO_DATABASE_COLUMN);
        } else {
            return responsibility.toUpperCase();
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData != null && THROW_EXCEPTION_IN_TO_ENTITY_ATTRIBUTE.equals(dbData)) {
            throw new RuntimeException(THROW_EXCEPTION_IN_TO_ENTITY_ATTRIBUTE);
        }
        return dbData;
    }
}
