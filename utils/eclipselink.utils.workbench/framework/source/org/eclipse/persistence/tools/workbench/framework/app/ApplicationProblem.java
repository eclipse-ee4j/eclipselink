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
package org.eclipse.persistence.tools.workbench.framework.app;

import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

/**
 * Define an interface describing the problems associated with an
 * application node.
 */
public interface ApplicationProblem {

    /**
     * Return the application node most closely associated with the problem.
     */
    ApplicationNode getSource();

    /**
     * Return a key that can be used to uniquely identify the type of problem.
     */
    String getMessageCode();

    /**
     * Return the problem's message.
     */
    String getMessage();

    /**
     * Print the problem on the specified stream.
     */
    void printOn(IndentingPrintWriter writer);

}
