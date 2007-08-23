/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.performance;

/**
 * <p><b>Purpose</b>: SmallProject is a concrete subclass of Project which adds no additional attributes.
 * <p><b>Description</b>: When the PROJ_TYPE is set to 'S' in the PROJECT table a SmallProject is instantiated.
 * No table definition is required and the descriptor is very simple.
 */
public class SmallProject extends Project {

    /**
     * Print the SmallProject's information.
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();

        writer.write("Small Project: ");
        writer.write(getName());
        writer.write(" ");
        writer.write(getDescription());
        writer.write("");
        return writer.toString();
    }
}