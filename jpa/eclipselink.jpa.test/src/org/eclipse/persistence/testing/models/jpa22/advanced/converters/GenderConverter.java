/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa22.advanced.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Gender;

@Converter(autoApply=false)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        if (attribute == null) {
            return null;
        } else {
            // Return M or F
            return attribute.name().substring(0, 1);
        }
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.equals("M")) {
            return Gender.Male;
        } else {
            return Gender.Female;
        }
    }
}
