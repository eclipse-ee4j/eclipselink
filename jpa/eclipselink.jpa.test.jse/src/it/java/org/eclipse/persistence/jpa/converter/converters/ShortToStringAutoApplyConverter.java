/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     12/05/2016-2.6 Jody Grassel
//       - 443546: Converter autoApply does not work for primitive types

package org.eclipse.persistence.jpa.converter.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply=true)
public class ShortToStringAutoApplyConverter implements AttributeConverter<Short, String> {
    public static boolean convertToDatabaseTriggered = false;
    public static boolean convertToEntityTriggered = false;
    
    public static Short ctdcVal = null;
    public static String cteaVal = null;
    
    public static void reset() {
        convertToDatabaseTriggered = false;
        convertToEntityTriggered = false;
        ctdcVal = null;
        cteaVal = null;
    }
    
    @Override
    public String convertToDatabaseColumn(Short attribute) {
        convertToDatabaseTriggered = true;
        ctdcVal = attribute;
        return Short.toString(attribute);
    }

    @Override
    public Short convertToEntityAttribute(String dbData) {
        convertToEntityTriggered = true;
        cteaVal = dbData;
        return Short.valueOf(dbData);
    }

}
