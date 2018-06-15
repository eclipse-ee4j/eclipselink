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
package org.eclipse.persistence.tools.workbench.mappings;

import java.util.ListResourceBundle;

public class MappingsBundle extends ListResourceBundle {

    private static final Object[][] contents = {
        {"help", "Usage: java {0} inputfile outputfile [logfile]"},
        {"generatingMight", "Your output file may not work correctly at runtime because one or more of your active descriptors or tables is incomplete.  Look through your project for descriptors or tables and assure everything is complete.  Continuing..."},
        {"errorGenerating", "Error Generating Output File..."},
        {"errorTheFollowingDescriptor...", "Error: The following descriptors do not have corresponding class files.  Please check your classpath."},
        {"EOJ", "Generation complete."},
        {"generationError", "An error occured during generation..."},
        {"projectsProblems", "{0} has {1} problem(s)"},
        {"notJ2eeProject", "{0}  is not a J2EE project"},
        {"error", "Error: "},
    };

    public Object[][] getContents() {
        return contents;
    }

}
