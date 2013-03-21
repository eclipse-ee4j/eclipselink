/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This is the superclass for all 
 * identification declaration nodes.
 * <p><b>Responsibilities</b>:<ul>
 * Manage the name of the identification variable.
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
    public void validate(ParseTreeContext context) {
        context.setScopeOfVariable(canonicalName);
    }
    
}
