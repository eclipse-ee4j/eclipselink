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
package org.eclipse.persistence.tools.workbench.framework;

/**
 * Interface for getting fairly static, application-wide information:
 * application name, version, etc.
 */
public interface Application {

    /**
     * Return the application's full product name,
     * e.g. "OracleAS TopLink 10g Workbench".
     * @see getShortProductName()
     */
    String getFullProductName();

    /**
     * Return the application's product name,
     * e.g. "OracleAS TopLink".
     * @see getFullProductName()
     * @see getShortProductName()
     */
    String getProductName();

    /**
     * Return the application's short product name,
     * e.g. "TopLink Workbench".
     * @see getFullProductName()
     */
    String getShortProductName();

    /**
     * Return the application's version,
     * e.g. "10.1.3".
     * @see getBuildNumber()
     */
    String getVersionNumber();

    /**
     * Return the application's full product name and version,
     * e.g. "OracleAS TopLink 10g Workbench 10.1.3".
     * @see getFullProductName()
     * @see getVersionNumber()
     */
    String getFullProductNameAndVersionNumber();

    /**
     * Return the application's build number,
     * typically in "yymmdd" format, e.g. "021125".
     * @see getVersionNumber()
     */
    String getBuildNumber();

    /**
     * Return the application's release designation
     */
    String getReleaseDesignation();

    /**
     * Return whether the application is in development mode.
     */
    boolean isDevelopmentMode();

    /**
     * Return whether the application is performing its first
     * execution in the current version. This allows us to perform
     * any necessary housekeeping.
     */
    boolean isFirstExecution();

}
