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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Simple interface for implementing the GOF Command design pattern.
 */
public interface Command {

    /**
     * Execute the command. The semantics of the command
     * is determined by the contract between the client and server.
     */
    void execute();

    Command NULL_INSTANCE =
        new Command() {
            public void execute() {
                // do nothing
            }
            public String toString() {
                return "NullCommand";
            }
        };

}
