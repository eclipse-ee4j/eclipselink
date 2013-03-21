/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
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
