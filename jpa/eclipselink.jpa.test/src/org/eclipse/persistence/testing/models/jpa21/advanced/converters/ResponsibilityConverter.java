/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     02/28/2013-2.5 Chris Delahunt
 *       - 402029: Application exceptions need to be wrapped in PersistenceException
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;

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
