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
