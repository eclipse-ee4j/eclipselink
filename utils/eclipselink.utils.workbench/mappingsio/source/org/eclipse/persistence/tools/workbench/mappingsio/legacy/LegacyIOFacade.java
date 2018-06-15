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
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * Provide a public facade to the legacy I/O stuff.
 */
public class LegacyIOFacade {

    public static MWProject read60Project(File file, Preferences preferences) {
        Project60IOManager ioManager = new Project60IOManager();
        return ioManager.read(file, preferences);
    }

    /**
     * disallow instantiation
     */
    private LegacyIOFacade() {
        super();
        throw new UnsupportedOperationException();
    }

}
