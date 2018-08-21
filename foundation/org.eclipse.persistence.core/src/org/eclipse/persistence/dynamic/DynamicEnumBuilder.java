/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     dclarke, mnorman - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//
package org.eclipse.persistence.dynamic;

import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

public class DynamicEnumBuilder {

    protected String className;
    protected AbstractDirectMapping adm;
    protected DynamicClassLoader dcl;
    protected String fieldName;

    public DynamicEnumBuilder(String className, AbstractDirectMapping adm,
        DynamicClassLoader dcl) {
        this.className = className;
        this.adm = adm;
        this.dcl = dcl;
    }

    public void addEnumLiteral(String literalLabel) {
        dcl.addEnum(className, literalLabel);
        EnumTypeConverter converter = (EnumTypeConverter)adm.getConverter();
        converter.addConversionValue(literalLabel, literalLabel);
    }

    public Enum getEnum() {
        EnumTypeConverter converter = (EnumTypeConverter)adm.getConverter();
        converter.convertClassNamesToClasses(dcl);
        Class enumClass = converter.getEnumClass();
        String literalLabel = (String)converter.getAttributeToFieldValues().
            keySet().iterator().next();
        return Enum.valueOf(enumClass, literalLabel);
    }
}
