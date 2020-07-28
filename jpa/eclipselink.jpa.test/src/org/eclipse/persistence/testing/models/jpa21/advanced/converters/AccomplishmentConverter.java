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
//     10/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=false)
public class AccomplishmentConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute + "!!!";
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
