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
package org.eclipse.persistence.tools.workbench.ant;

import java.util.ListResourceBundle;

public class AntExtensionBundle extends ListResourceBundle {

    private static final Object[][] contents = {
        { "ignoringProblems", "Ignoring: {0} problem(s) ( "},
        { "numberOfProblems", "{0}.mwp project has {1} problem(s)."},
        { "errorWhileExporting", "An error occurred while exporting project: {0}"},
        { "errorWhileValidating", "An error occurred while validating project: {0}"},
        { "couldNotLogin", "Could not log in to database. Make sure your database server is running: {0}"},
        { "notDefined", "{0} not defined"},
        { "errorDescription", "Error: {0}"},
        { "exportingEjbJar", "Exporting: {0}"},
        { "exportingXml", "Exporting: {0}"},
        { "generatingReport", "Generating report: {0}"},
        { "usingSysProperties", "Using System properties: {0}"},
        { "usingClasspath", "Using CLASSPATH: {0}"},
        { "runnerClassNotFound", "ProjectRunner Class Not Found: {0}"},
        { "instantiationExceptionAtInstantiation", "Instantiation Exception When Instantiating: {0}"},
        { "illegalAccessExceptionAtInstantiation", "Illegal Access Exception When Instantiating: {0}"},
        { "illegalArgumentExceptionAtInvocation", "Illegal Argument Exception When Invoking: {0}"},
        { "illegalAccessExceptionAtInvocation", "Illegal Access Exception When Invoking: {0}"},
        { "expectIntegerReturningType", "Expecting Integer Returning Type For Method: {0}"},
        { "executeMethodNotFound", "Execute Method Not Found In: {0}"},
        { "executeMethodCannotBeNull", "Execute Method Parameter Cannot Be Null"},
        { "notNotAIgnoreErrorSet", "{0} doesn\'t denote a IgnoreErrorSet"},
        { "notNotALoginSpec", "{0} doesn\'t denote a LoginSpec"},
    };

    public Object[][] getContents() {
        return contents;
    }
}
