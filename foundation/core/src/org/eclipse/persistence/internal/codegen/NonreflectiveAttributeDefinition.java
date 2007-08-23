/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

    protected void adjustTypeNames(Map typeNameMap) {
        adjustType(typeNameMap);
        super.adjustTypeNames(typeNameMap);
    }

    protected String getTypeName() {
        return type;
    }

    public void setType(String typeName) {
        this.type = typeName;
    }
}