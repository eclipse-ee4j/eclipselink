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
package org.eclipse.persistence.testing.models.jpa.performance;

/**
 * <p><b>Purpose</b>: SmallProject is a concrete subclass of Project which adds no additional attributes.
 * <p><b>Description</b>: When the PROJ_TYPE is set to 'S' in the PROJECT table a SmallProject is instantiated.
 * No table definition is required and the descriptor is very simple.
 */
//@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
