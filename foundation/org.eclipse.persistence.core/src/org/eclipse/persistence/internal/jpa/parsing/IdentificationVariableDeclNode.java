/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.parsing;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This is the superclass for all
 * identification declaration nodes.
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li>Manage the name of the identification variable.</li>
 * </ul>
 */
public abstract class IdentificationVariableDeclNode extends Node {

    /** The variable name as specified. */
    private String name;

    /** The canonical representation of the variable name. */
    private String canonicalName;

    /** */
    public String getVariableName() {
        return name;
    }

    /** */
    public void setVariableName(String name) {
        this.name = name;
        this.canonicalName = calculateCanonicalName(name);
    }

    /** */
    public String getCanonicalVariableName() {
        return canonicalName;
    }

    /** */
    public static String calculateCanonicalName(String name) {
        return name.toLowerCase();
    }

    /** */
    public Node getPath() {
        return null;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        context.setScopeOfVariable(canonicalName);
    }

}
