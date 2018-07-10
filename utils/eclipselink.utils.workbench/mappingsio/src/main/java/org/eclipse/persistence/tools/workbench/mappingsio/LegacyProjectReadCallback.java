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
package org.eclipse.persistence.tools.workbench.mappingsio;

/**
 * This interface defines a callback object that checks whether a project
 * reader should continue with the reading of a legacy project.
 */
public interface LegacyProjectReadCallback {

    /**
     * Check whether the Project I/O Manager (Project Reader) should
     * continue with the reading of a project of the specified version.
     * Throw a client-determined runtime exception if the project should
     * not be read.
     */
    void checkLegacyRead(String schemaVersion);


    LegacyProjectReadCallback NULL_INSTANCE =
        new LegacyProjectReadCallback() {
            public void checkLegacyRead(String schemaVersion) {
                // do nothing - allowing the legacy project read to continue
            }
            public String toString() {
                return "NullLegacyProjectReadCallback";
            }
        };

}
