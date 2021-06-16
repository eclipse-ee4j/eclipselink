/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.codegen;

import java.util.*;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model an attribute for code generation purposes.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public abstract class AttributeDefinition extends CodeDefinition {
    protected String initialValue;

    public AttributeDefinition() {
    }

    /**
     * Parses the initial value, removing the package name for each type
     * (and adding the appropriate import) if the type is
     * unambiguous.
     */
    private void adjustInitialValue(Map typeNameMap) {
        if (getInitialValue() == null) {
            return;
        }

        StringBuilder initialValue = new StringBuilder(getInitialValue());
        Set typeNames = parseForTypeNames(initialValue.toString());

        for (Iterator i = typeNames.iterator(); i.hasNext();) {
            String typeName = (String)i.next();
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

    protected void adjustTypeNames(Map typeNameMap) {
        adjustInitialValue(typeNameMap);
    }

    public String getInitialValue() {
        return initialValue;
    }

    protected abstract String getTypeName();

    /**
     * Used for calculating imports.  @see org.eclipse.persistence.internal.codegen.ClassDefinition#calculateImports()
     */
    protected void putTypeNamesInMap(Map typeNameMap) {
        putTypeNameInMap(getTypeName(), typeNameMap);

        for (Iterator i = parseForTypeNames(getInitialValue()).iterator(); i.hasNext();) {
            putTypeNameInMap((String)i.next(), typeNameMap);
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
