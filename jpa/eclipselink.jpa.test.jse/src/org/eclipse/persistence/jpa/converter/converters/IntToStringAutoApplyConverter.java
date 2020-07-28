/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
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
//     12/05/2016-2.6 Jody Grassel
//       - 443546: Converter autoApply does not work for primitive types

package org.eclipse.persistence.jpa.converter.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class IntToStringAutoApplyConverter implements AttributeConverter<Integer, String> {
    public static boolean convertToDatabaseTriggered = false;
    public static boolean convertToEntityTriggered = false;
    
    public static Integer ctdcVal = null;
    public static String cteaVal = null;
    
    public static void reset() {
        convertToDatabaseTriggered = false;
        convertToEntityTriggered = false;
        ctdcVal = null;
        cteaVal = null;
    }
    
    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        convertToDatabaseTriggered = true;
        ctdcVal = attribute;
        return Integer.toString(attribute);
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        convertToEntityTriggered = true;
        cteaVal = dbData;
        return Integer.valueOf(dbData);
    }

}
