/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.mappings.TypedAssociation;

/**
 * <p><b>Purpose</b>: Used to define the query argument mapping.
 * This is used for the deployment XML mapping.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class QueryArgument extends TypedAssociation {

    /** The type of the query argument */
    protected Class type;
    protected String typeName;// bug 3256198 types can now be set by name
    protected boolean nullable;

    /**
     * Default constructor.
     */
    public QueryArgument() {
        super();
    }

    public QueryArgument(String argumentName, Object value, Class type) {
        super(argumentName, value);
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Class getType() {
        return type;
    }

    /**
     * INTERNAL:
     * Return the classname of the type.
     */
    public String getTypeName() {
        return typeName;
    }

    public void setType(Class type) {
        this.type = type;
        if (type != null) {
            this.typeName = type.getName();
        }
    }

    /**
     * INTERNAL:
     * Set the classname of the type.
     * This information will be used to avoid Mapping Workbench classpath dependencies by
     * allowing the type to be set by classname instead of the class itself.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
