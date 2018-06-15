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


/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model an attribute for code generation purposes,
 * using a java.lang.Class for the attribute type.
 *
 * @since TopLink 5.0
 * @author Paul Fullbright
 */
public class ReflectiveAttributeDefinition extends AttributeDefinition {
    protected Class type;

    public ReflectiveAttributeDefinition() {
        this.type = null;
    }

    public Class getType() {
        return type;
    }

    @Override
    protected String getTypeName() {
        //fixed for CR#4228
        if (getType().isArray()) {
            String componentType = getType().getComponentType().getName();
            return componentType + "[]";
        } else {
            return getType().getName();
        }
    }

    public void setType(Class type) {
        this.type = type;
    }
}
