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
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;

/**
 * <p>
 * <b>Purpose</b>: An abstract class that can be subclassed when internal
 * commands need to be added.
 * <p>
 * <b>Description</b>: This command provides a framework for execution of
 * commands that are internal to EclipseLink. Commands that subclass this class will
 * not get converted or handed off to the CommandProcessor to be executed. They
 * will instead be executed by the RemoteCommandManager directly. Subclasses
 * should implement the executeWithRCM method to perform the execution steps that
 * are relevant to the command.
 * <p>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public abstract class RCMCommand extends Command {
    public abstract void executeWithRCM(RemoteCommandManager rcm);

    public boolean isInternalCommand() {
        return true;
    }
}
