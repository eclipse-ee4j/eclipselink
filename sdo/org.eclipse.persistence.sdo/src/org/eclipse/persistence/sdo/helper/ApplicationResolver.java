/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - October 22/2010 - 2.1.2 - Initial implementation
package org.eclipse.persistence.sdo.helper;

/**
 * This class provides a means for the user to return application-specific
 * information, such as the application name, which will be used when the
 * logic in SDOHelperContext fails.
 *
 */
public abstract class ApplicationResolver {

    /**
     * Return the application name for the currently executing application.
     *
     * This method will be utilized if the logic in SDOHelperContext fails
     * to determine the name of the current application, meaning it will
     * be used as a last resort before keying on the currently executing
     * thread's context loader.
     *
     * @return non-empty String which indicates the application name
     */
    public abstract String getApplicationName();
}
