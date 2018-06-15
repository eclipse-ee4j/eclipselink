/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.codegen;

import java.util.Map;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model an attribute for code generation purposes,
 * using a java.lang.String for the attribute type.
 *
 * @since TopLink 5.0
 * @author Paul Fullbright
 */
public class NonreflectiveAttributeDefinition extends AttributeDefinition {
    protected String type;

    public NonreflectiveAttributeDefinition() {
        this.type = "";
    }

    private void adjustType(Map typeNameMap) {
        String adjustedTypeName = adjustTypeName(getTypeName(), typeNameMap);

        if (!getTypeName().equals(adjustedTypeName)) {
            setType(adjustedTypeName);
        }
    }

    @Override
    protected void adjustTypeNames(Map typeNameMap) {
        adjustType(typeNameMap);
        super.adjustTypeNames(typeNameMap);
    }

    @Override
    protected String getTypeName() {
        return type;
    }

    public void setType(String typeName) {
        this.type = typeName;
    }
}
