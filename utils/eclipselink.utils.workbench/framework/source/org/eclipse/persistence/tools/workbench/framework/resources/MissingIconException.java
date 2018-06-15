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
package org.eclipse.persistence.tools.workbench.framework.resources;


/**
 * This is the exception that will be thrown if an icon repository
 * cannot find the icon for the specified key.
 */
public class MissingIconException extends MissingResourceException {

    public MissingIconException(String message, String key) {
        super(message, key);
    }

}
