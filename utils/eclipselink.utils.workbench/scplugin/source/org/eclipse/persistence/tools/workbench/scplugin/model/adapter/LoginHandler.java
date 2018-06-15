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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

/**
 * Defines the interface to an object that contains a DatabaseLogin.
 *
 * @see DatabaseLoginConfig
 *
 * @author Tran Le
 */
public interface LoginHandler {

    public LoginAdapter getLogin();

    public void setExternalConnectionPooling( boolean value);

    public boolean usesExternalConnectionPooling();

}
