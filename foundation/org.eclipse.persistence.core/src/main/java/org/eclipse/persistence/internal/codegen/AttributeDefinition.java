/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.codegen;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model an attribute for code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public abstract class AttributeDefinition extends CodeDefinition {
    protected String initialValue;

    protected AttributeDefinition() {
    }

    /**
     * Parses the initial value, removing the package name for each type
     * (and adding the appropriate import) if the type is
     * unambiguous.
     */
    private void adjustInitialValue(Map<String, Set<String>> typeNameMap) {
        if (getInitialValue() == null) {
            return;
        }

        StringBuilder initialValue = new StringBuilder(getInitialValue());
        Set<String> typeNames = parseForTypeNames(initialValue.toString());

        for (Iterator<String> i = typeNames.iterator(); i.hasNext();) {
            String typeName = i.next();
            String adjustedTypeName = adjustTypeName(typeName, typeNameMap);

            if (!typeName.equals(adjustedTypeName)) {
                int typeNameStartIndex = initialValue.toString().indexOf(typeName);

                while (typeNameStartIndex != -1) {
                    initialValue.replace(typeNameStartIndex, typeNameStartIndex + typeName.length(), adjustedTypeName);
                    typeNameStartIndex = initialValue.toString().indexOf(typeName);
                }
            }
        }

        setInitialValue(initialValue.toString());
    }

    protected void adjustTypeNames(Map<String, Set<String>> typeNameMap) {
        adjustInitialValue(typeNameMap);
    }

    public String getInitialValue() {
        return initialValue;
    }

    protected abstract String getTypeName();

    /**
     * Used for calculating imports.  @see org.eclipse.persistence.internal.codegen.ClassDefinition#calculateImports()
     */
    protected void putTypeNamesInMap(Map<String, Set<String>> typeNameMap) {
        putTypeNameInMap(getTypeName(), typeNameMap);

        for (Iterator<String> i = parseForTypeNames(getInitialValue()).iterator(); i.hasNext();) {
            putTypeNameInMap(i.next(), typeNameMap);
        }
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public void writeBody(CodeGenerator generator) {
        generator.writeType(getTypeName());
        generator.writeType(" ");
        generator.write(getName());

        if (getInitialValue() != null) {
            generator.write(" = ");
            generator.write(getInitialValue());
        }

        generator.write(";");
    }
}
