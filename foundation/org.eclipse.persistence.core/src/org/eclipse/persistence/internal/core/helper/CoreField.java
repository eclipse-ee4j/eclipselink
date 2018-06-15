/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.helper;

public interface CoreField {

    /**
     * Return the unqualified name of the field.
     */
    public String getName();

    public Class getType();

    /**
     * Set the unqualified name of the field.
     */
    public void setName(String name);

    /**
     * Set the Java class type that corresponds to the field.
     * The JDBC type is determined from the class type,
     * this is used to optimize performance, and for binding.
     */
    public void setType(Class type);

}
