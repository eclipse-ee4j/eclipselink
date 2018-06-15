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

/**
 * Defines the interface supported by the SessionValidator.
 */
public interface SessionValidatorInterface {
    /**
     * Test TopLink deployment descriptor XML by running TopLink
     *
     * Returns 0 if the generation is successful.
     */
    int execute( String sessionName, String sessionsFileName, String url, String driverclass, String user, String password);

}

