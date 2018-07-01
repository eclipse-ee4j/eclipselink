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
package org.eclipse.persistence.tools.workbench.test.ant;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

/**
 *  RelationalProjectRunnerTests
 */
public abstract class RelationalProjectRunnerTests extends ProjectRunnerTests {

    public RelationalProjectRunnerTests( String name) {
        super( name);
    }
    /**
     * Post building MW project.
     */
    protected void postBuildProject( MWProject project) {

        this.configureDeploymentLogin( project);
    }
    /**
     * Setup Login Spec to Oracle.
     */
    protected void configureDeploymentLogin( MWProject project) {
        MWDatabase database = project.getDatabase();
        database.setDeploymentLoginSpec( database.loginSpecNamed("MySQL"));
    }
}
