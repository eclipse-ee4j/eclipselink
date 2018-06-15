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
package org.eclipse.persistence.tools.workbench.ant;

import java.util.Vector;

/**
 * Defines the interface supported by the ProjectValidator.
 */
public interface ProjectValidatorInterface {
    /**
     * Validates a Workbench project.
     *
     * Returns 0 if successful.
     */
        int execute( String projectFile, String reportfile, String reportformat, Vector ignoreErrorCodes);

}
