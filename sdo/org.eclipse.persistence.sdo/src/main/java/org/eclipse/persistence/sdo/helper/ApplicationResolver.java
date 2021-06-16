/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
