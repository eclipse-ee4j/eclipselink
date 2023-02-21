/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - Initial implementation
package org.eclipse.persistence.testing.tests.jgroups;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.eclipse.persistence.sessions.coordination.CommandProcessor;

public class JGroupsCommandProcessor extends AbstractSession implements CommandProcessor {

    private String commandContent = null;

    @Override
    public void processCommand(Object command) {
        commandContent = command.toString();
    }

    @Override
    public CommandManager getCommandManager() {
        return null;
    }

    @Override
    public void setCommandManager(CommandManager commandManager) {

    }

    @Override
    public boolean shouldLogMessages(int logLevel) {
        return false;
    }

    @Override
    public void logMessage(int logLevel, String message) {

    }

    @Override
    public void incrementProfile(String counter) {

    }

    @Override
    public void updateProfile(String info, Object value) {

    }

    @Override
    public void startOperationProfile(String operationName) {

    }

    @Override
    public void endOperationProfile(String operationName) {

    }

    @Override
    public Object handleException(RuntimeException exception) {
        return null;
    }

    @Override
    public Login getDatasourceLogin() {
        Login login = new DatabaseLogin();
        return login;
    }

    public String getCommandContent() {
        return commandContent;
    }
}
