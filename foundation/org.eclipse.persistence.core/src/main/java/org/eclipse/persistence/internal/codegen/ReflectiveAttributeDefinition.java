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
