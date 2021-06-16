/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
