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
package org.eclipse.persistence.sessions.coordination;

import org.eclipse.persistence.sessions.coordination.Command;

/**
 * <p>
 * <b>Purpose</b>: Define a pluggable conversion interface that can be supplied
 * by the application
 * <p>
 * <b>Description</b>: The implementation class of this interface should be set
 * on the remote command manager through the setCommandConverter() method. The
 * implementation class will get invoked, through its convertToEclipseLinkCommand(),
 * to convert the application command format into a TopLink Command object that
 * can be propagated throughout the cluster. Similarly, convertToUserCommand()
 * will be invoked on the implementation class to give the application an
 * opportunity to convert the EclipseLink Command object into an object that is suitable
 * for being processed by the application.
 *
 * @see Command
 * @see CommandManager
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public interface CommandConverter {

    /**
     * PUBLIC:
     * Convert a command from its application-specific format to
     * a EclipseLink Command object.
     *
     * @param command An application-formatted command
     * @return The converted Command object that will be sent to remote services
     */
    Command convertToEclipseLinkCommand(Object command);

    /**
     * PUBLIC:
     * Convert a EclipseLink Command object into its application-specific
     * format to a
     *
     * @param command An application-formatted command
     * @return The converted Command object that will be sent to be remotely executed
     */
    Object convertToUserCommand(Command command);
}
